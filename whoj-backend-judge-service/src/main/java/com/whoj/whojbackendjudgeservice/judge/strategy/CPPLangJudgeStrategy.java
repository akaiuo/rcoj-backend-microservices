package com.whoj.whojbackendjudgeservice.judge.strategy;

import com.whoj.whojbackendmodel.model.codesandbox.JudgeInfo;
import com.whoj.whojbackendmodel.model.dto.question.JudgeCase;
import com.whoj.whojbackendmodel.model.dto.question.JudgeConf;
import com.whoj.whojbackendmodel.model.enums.JudgeInfoMessageEnum;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * c/c++语言判题处理
 */
public class CPPLangJudgeStrategy implements JudgeStrategy {

    @Override
    public JudgeContext doJudge(JudgeContext judgeContext) {
        List<String> runOutputList = judgeContext.getOutputList(); // 运行输出
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        JudgeConf judgeConf = judgeContext.getJudgeConf(); //判题限制时间、内存
        List<String> outputList = judgeCaseList.stream().map(JudgeCase::getOutput).toList(); // 正确输出
        List<Long> timeList = judgeContext.getTimeList();
        List<Long> memoList = judgeContext.getMemoList();
        List<String> messageList = new LinkedList<>();
        String message = JudgeInfoMessageEnum.ACCEPTED.getText();
        Long maxTime = 0L;
        Long maxMemo = 0L;
        Set<JudgeInfoMessageEnum> messageEnumSet = new HashSet<>(); // 用于判断 “多种错误” 情况

        for (int i = 0; i < judgeCaseList.size(); i++) {
            String output = outputList.get(i); // 正确输出
            maxTime = Math.max(maxTime, timeList.get(i));
            maxMemo = Math.max(maxMemo, memoList.get(i));
            // 判断时间
            if (timeList.get(i) >= judgeConf.getTimeLimit()) {
                message = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getText();
                messageEnumSet.add(JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED);
                continue;
            }
            // 判断内存
            if (memoList.get(i) / 1000 >= judgeConf.getMemoLimit()) {
                message = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED.getText();
                messageEnumSet.add(JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED);
                continue;
            }
            //判断答案
            if (!runOutputList.get(i).equals(output)) {
                message = JudgeInfoMessageEnum.WRONG_ANSWER.getText();
                messageEnumSet.add(JudgeInfoMessageEnum.WRONG_ANSWER);
                continue;
            }
        }
        JudgeInfo judgeInfo = JudgeInfo.builder()
                .maxTime(maxTime)
                .maxMemo(maxMemo)
                .message(messageEnumSet.size() >= 2 ? JudgeInfoMessageEnum.MANY_ERROR.getText() : message)
                .build();

        return JudgeContext.builder()
                .judgeInfo(judgeInfo)
                .messageList(messageList)
                .build();
    }
}
