package com.whoj.whojbackendjudgeservice.judge;

import cn.hutool.json.JSONUtil;
import com.whoj.whojbackcommon.common.ErrorCode;
import com.whoj.whojbackcommon.exception.BusinessException;
import com.whoj.whojbackendjudgeservice.judge.codeSandbox.CodeSandbox;
import com.whoj.whojbackendjudgeservice.judge.codeSandbox.CodeSandboxFactory;
import com.whoj.whojbackendjudgeservice.judge.codeSandbox.CodeSandboxProxy;
import com.whoj.whojbackendjudgeservice.judge.codeSandbox.enums.ExecuteStateEnum;
import com.whoj.whojbackendjudgeservice.judge.codeSandbox.model.ExecuteCodeRequest;
import com.whoj.whojbackendjudgeservice.judge.codeSandbox.model.ExecuteCodeResponse;
import com.whoj.whojbackendjudgeservice.judge.strategy.JudgeContext;
import com.whoj.whojbackendjudgeservice.message.MessageProducer;
import com.whoj.whojbackendmodel.model.codesandbox.JudgeInfo;
import com.whoj.whojbackendmodel.model.dto.question.JudgeCase;
import com.whoj.whojbackendmodel.model.dto.question.JudgeConf;
import com.whoj.whojbackendmodel.model.dto.question.JudgeInfoDetail;
import com.whoj.whojbackendmodel.model.entity.Question;
import com.whoj.whojbackendmodel.model.entity.QuestionSubmit;
import com.whoj.whojbackendmodel.model.enums.JudgeInfoMessageEnum;
import com.whoj.whojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.whoj.whojbackendmodel.model.message.JudgeSubmit;
import com.whoj.whojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionFeignClient questionFeignClient;

    @Resource
    private JudgeManager judgeManager;

    @Resource
    private MessageProducer messageProducer;

    @Value("${codeSandbox.type:example}")
    private String type;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1）传入题目的id，获取到对应的题目提交信息（包含代码，编程语言等）
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionFeignClient.getQuestionById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 2）如果题目提交状态不为等待中，就不重复执行了
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }
        // 3）更改判题（题目提交）的状态为判题中，防止重复运行，也能让用户即时看到状态
        QuestionSubmit update = new QuestionSubmit();
        update.setId(questionSubmit.getId());
        update.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean isUpdate = questionFeignClient.updateQuestionSubmitById(update);
        if (!isUpdate) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        // 4）调用沙箱，获取到执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        List<JudgeCase> judgeCaseList = JSONUtil.toList(question.getJudgeCase(), JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).toList();
        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code(questionSubmit.getCode())
                .inputList(inputList)
                .lang(questionSubmit.getLang())
                .time(JSONUtil.toBean(question.getJudgeConf(), JudgeConf.class).getTimeLimit())
                .build();
        ExecuteCodeResponse response = codeSandbox.executeCode(request);
        // 对特殊情况处理
        if (Objects.equals(response.getStatus(), ExecuteStateEnum.COMPILE_ERROR.getValue())) {
            JudgeInfo judgeInfo = JudgeInfo.builder().message(JudgeInfoMessageEnum.COMPILE_ERROR.getText()).build();
            update.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
            update.setExecuteMessage(response.getMessage());
            update.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());
            isUpdate = questionFeignClient.updateQuestionSubmitById(update);
            return questionFeignClient.getQuestionSubmitById(questionSubmitId);
        }
        if (Objects.equals(response.getStatus(), ExecuteStateEnum.NO_AUTH.getValue())) {
            JudgeInfo judgeInfo = JudgeInfo.builder().message(JudgeInfoMessageEnum.SYSTEM_ERROR.getText()).build();
            update.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
            update.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());
            isUpdate = questionFeignClient.updateQuestionSubmitById(update);
            return questionFeignClient.getQuestionSubmitById(questionSubmitId);
        }
        // 5）根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeConf judgeConf = JSONUtil.toBean(question.getJudgeConf(), JudgeConf.class);
        JudgeContext judgeContext = JudgeContext.builder()
                .JudgeConf(judgeConf)
                .judgeInfo(response.getJudgeInfo())
                .judgeCaseList(judgeCaseList)
                .outputList(response.getOutputList())
                .lang(questionSubmit.getLang())
                .memoList(response.getMemoList())
                .timeList(response.getTimeList())
                .errorOutputlist(response.getErrorOutputList())
                .build();
        // 策略模式选择不同判题机制
        JudgeContext doJudge = judgeManager.doJudge(judgeContext);
        // 6）修改数据库中的判题结果
        JudgeInfoDetail judgeInfoDetail = JudgeInfoDetail.builder()
                .messageList(doJudge.getMessageList())
                .timeList(response.getTimeList())
                .memoList(response.getMemoList())
                .errorOutputList(response.getErrorOutputList())
                .build();
        update = new QuestionSubmit();
        update.setId(questionSubmitId);
        update.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());
        update.setJudgeInfo(JSONUtil.toJsonStr(doJudge.getJudgeInfo()));
        update.setJudgeInfoDetail(JSONUtil.toJsonStr(judgeInfoDetail));
        isUpdate = questionFeignClient.updateQuestionSubmitById(update);
        if (!isUpdate)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        JudgeSubmit judgeSubmit = new JudgeSubmit(questionId, doJudge.getJudgeInfo().getMessage().equals(JudgeInfoMessageEnum.ACCEPTED.getText()));
        messageProducer.sendMessage("code_exchange", "my_routingKey", JSONUtil.toJsonStr(judgeSubmit));
        return questionFeignClient.getQuestionSubmitById(questionSubmitId);
    }
}
