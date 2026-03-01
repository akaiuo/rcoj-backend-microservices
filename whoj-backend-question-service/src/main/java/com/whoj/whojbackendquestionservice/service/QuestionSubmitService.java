package com.whoj.whojbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.whoj.whojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.whoj.whojbackendmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.whoj.whojbackendmodel.model.entity.QuestionSubmit;
import com.whoj.whojbackendmodel.model.entity.User;
import com.whoj.whojbackendmodel.model.vo.QuestionSubmitVO;
import reactor.core.publisher.Flux;

/**
* @author admin
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2024-09-04 17:02:33
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest 题目提交信息
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     * TODO 题目提交（内部服务）
     */

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 根据提交id获取提交的详细信息，包括每个用例的时间、内存、报错
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目提交列表，可查看所有人的判题信息，不能查看代码及详细判题信息
     *
     * @param questionSubmitPage
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage);

    /**
     * AI执行错误分析
     * @param questionSubmitId
     * @param user
     * @return
     */
    public Flux<String> errorAIAnalysis (long questionSubmitId, User user);
}
