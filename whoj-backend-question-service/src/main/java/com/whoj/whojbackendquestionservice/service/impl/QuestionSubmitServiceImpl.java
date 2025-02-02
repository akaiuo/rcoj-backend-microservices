package com.whoj.whojbackendquestionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whoj.whojbackcommon.common.ErrorCode;
import com.whoj.whojbackcommon.constant.CommonConstant;
import com.whoj.whojbackcommon.exception.*;
import com.whoj.whojbackcommon.utils.SqlUtils;
import com.whoj.whojbackendmodel.model.dto.questionsubmit.*;
import com.whoj.whojbackendmodel.model.entity.*;
import com.whoj.whojbackendmodel.model.enums.LangEnum;
import com.whoj.whojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.whoj.whojbackendmodel.model.vo.QuestionSubmitVO;
import com.whoj.whojbackendquestionservice.mapper.*;
import com.whoj.whojbackendquestionservice.message.MessageProducer;
import com.whoj.whojbackendquestionservice.service.QuestionService;
import com.whoj.whojbackendquestionservice.service.QuestionSubmitService;
import com.whoj.whojbackendserviceclient.service.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
* @author admin
* @description 针对表【question_submit(题目提交)】的数据库操作Service实现
* @createDate 2024-09-04 17:02:33
*/
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
    implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private MessageProducer messageProducer;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        //判断编程语言是否合法
        String lang = questionSubmitAddRequest.getLang();
        LangEnum langEnum = LangEnum.getEnumByValue(lang);
        if (langEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "提交语言错误");
        }
        long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionSubmitAddRequest.getQuestionId());
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        // 每个用户串行提交题目
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLang(lang);
        // 设置初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        Long questionSubmitId = questionSubmit.getId();
        // 执行判题服务
        messageProducer.sendMessage("code_exchange", "my_routingKey", String.valueOf(questionSubmitId));
        return questionSubmitId;
    }

    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitSubmitQueryRequest == null) {
            return queryWrapper;
        }

        String lang = questionSubmitSubmitQueryRequest.getLang();
        Integer status = questionSubmitSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitSubmitQueryRequest.getUserId();
        String sortField = questionSubmitSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(lang), "lang", lang);
        queryWrapper.like(!ObjectUtils.isEmpty(userId), "userId", userId);
        queryWrapper.like(!ObjectUtils.isEmpty(questionId), "questionId", questionId);
        queryWrapper.like(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 根据提交id获取提交的详细信息，包括代码、每个用例的时间、内存、报错
     * @param questionSubmit 包含题目提交id的对象
     * @param loginUser 当前登录的用户 （判断是否为该题提交者或管理员）
     * @return 题目提交详细判题信息
     */
    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        if (!Objects.equals(loginUser.getId(), questionSubmit.getUserId()) && !userFeignClient.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限");
        }
        return questionSubmitVO;
    }

    /**
     * 分页获取题目提交列表，可查看所有人的判题信息，不能查看代码及详细判题信息
     * @param questionSubmitPage 查询到的分页数据
     * @return 脱敏结果
     */
    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (questionSubmitList.isEmpty()) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream().map(questionSubmit -> {
            QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
            // 脱敏
            questionSubmitVO.setCode(null);
            questionSubmitVO.setJudgeInfoDetail(null);
            return questionSubmitVO;
        }).toList();
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

}
