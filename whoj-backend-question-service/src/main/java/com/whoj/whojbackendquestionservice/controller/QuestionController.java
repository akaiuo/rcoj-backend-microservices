package com.whoj.whojbackendquestionservice.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whoj.whojbackcommon.annotation.AuthCheck;
import com.whoj.whojbackcommon.common.BaseResponse;
import com.whoj.whojbackcommon.common.DeleteRequest;
import com.whoj.whojbackcommon.common.ErrorCode;
import com.whoj.whojbackcommon.common.ResultUtils;
import com.whoj.whojbackcommon.constant.UserConstant;
import com.whoj.whojbackcommon.exception.BusinessException;
import com.whoj.whojbackcommon.exception.ThrowUtils;
import com.whoj.whojbackendmodel.model.dto.comment.QuestionCommentAddRequest;
import com.whoj.whojbackendmodel.model.dto.comment.QuestionCommentQueryRequest;
import com.whoj.whojbackendmodel.model.dto.question.*;
import com.whoj.whojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.whoj.whojbackendmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.whoj.whojbackendmodel.model.entity.QuestionComment;
import com.whoj.whojbackendmodel.model.entity.Question;
import com.whoj.whojbackendmodel.model.entity.QuestionSubmit;
import com.whoj.whojbackendmodel.model.entity.User;
import com.whoj.whojbackendmodel.model.vo.CommentVO;
import com.whoj.whojbackendmodel.model.vo.QuestionSubmitVO;
import com.whoj.whojbackendmodel.model.vo.QuestionVO;
import com.whoj.whojbackendquestionservice.service.CommentService;
import com.whoj.whojbackendquestionservice.service.QuestionService;
import com.whoj.whojbackendquestionservice.service.QuestionSubmitService;
import com.whoj.whojbackendserviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题目接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private CommentService commentService;

    // region 增删改查

    /**
     * 创建题目
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        List<JudgeCase> judgeCaseList = questionAddRequest.getJudgeCase();
        if (judgeCaseList != null) {
            question.setJudgeCase(JSONUtil.toJsonStr(judgeCaseList));
        }
        JudgeConf judgeConf = questionAddRequest.getJudgeConf();
        if (judgeConf != null) {
            question.setJudgeConf(JSONUtil.toJsonStr(judgeConf));
        }
        questionService.validQuestion(question, true);
        User loginUser = userFeignClient.getLoginUser(request);
        question.setUserId(loginUser.getId());
        question.setFavourNum(0);
        question.setThumbNum(0);
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除题目
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userFeignClient.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userFeignClient.isAdmin(user)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = questionService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        List<JudgeCase> judgeCaseList = questionUpdateRequest.getJudgeCase();
        if (judgeCaseList != null) {
            question.setJudgeConf(JSONUtil.toJsonStr(judgeCaseList));
        }
        JudgeConf judgeConf = questionUpdateRequest.getJudgeConf();
        if (judgeConf != null) {
            question.setJudgeConf(JSONUtil.toJsonStr(judgeConf));
        }
        // 参数校验
        questionService.validQuestion(question, false);
        long id = questionUpdateRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取题目vo
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }

    /**
     * 根据 id 获取题目
     *
     * @param id
     * @return
     */
    @GetMapping("/get/entity")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Question> getQuestionById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userFeignClient.isAdmin(userFeignClient.getLoginUser(request));
        if (!isAdmin) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(question);
    }

    /**
     * 分页获取题目列表（仅管理员）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                   HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionPage);
    }

    /**
     * 分页获取题目列表（封装类）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
            HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 分页获取当前用户创建的题目列表
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
            HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    // endregion

    /**
     * 编辑题目（用户）
     *
     * @param questionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        List<String> tags = questionEditRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        List<JudgeCase> judgeCaseList = questionEditRequest.getJudgeCase();
        if (judgeCaseList != null) {
            question.setJudgeCase(JSONUtil.toJsonStr(judgeCaseList));
        }
        JudgeConf judgeConf = questionEditRequest.getJudgeConf();
        if (judgeConf != null) {
            question.setJudgeConf(JSONUtil.toJsonStr(judgeConf));
        }
        // 参数校验
        questionService.validQuestion(question, false);
        User loginUser = userFeignClient.getLoginUser(request);
        long id = questionEditRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userFeignClient.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }

    /**
     * 提交作答
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return resultNum 提交记录id
     */

    @PostMapping("/submit/do")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                               HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能提交
        final User loginUser = userFeignClient.getLoginUser(request);
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(questionSubmitId);
    }

    /**
     * 根据提交id获取提交的详细信息，包括每个用例的时间、内存、报错
     */
    @PostMapping("/submit/vo")
    public BaseResponse<QuestionSubmitVO> getQuestionSubmitVOById(long submitId, HttpServletRequest request) {
        QuestionSubmit questionSubmit = questionSubmitService.getById(submitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到提交信息");
        }
        User loginUser = userFeignClient.getLoginUser(request);
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVO(questionSubmit, loginUser));
    }

    /**
     * 分页获取题目提交列表，可查看所有人的判题信息，不能查看代码及详细判题信息
     * (关于自己的提交，可通过搜索过滤)
     */
    @PostMapping("/submit/page/vo")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitVoByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        if (questionSubmitQueryRequest.getUser() == null){
            questionSubmitQueryRequest.setUser("");
        }
        // 从数据库里中查询原始的题目提交分页信息
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest)); // 这里用于过滤
        // 返回脱敏信息
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage));
    }

    /**
     * 根据题目id分页获取评论列表
     * @param questionCommentQueryRequest
     * @param request
     * @return 查询出的评论分页列表
     */
    @PostMapping("/comment/list/page/vo")
    public BaseResponse<Page<CommentVO>> postCommentsByQuestionId(@RequestBody QuestionCommentQueryRequest questionCommentQueryRequest, HttpServletRequest request) {
        long current = questionCommentQueryRequest.getCurrent();
        long size = questionCommentQueryRequest.getPageSize();
        Page<QuestionComment> commentPage = commentService.page(new Page<>(current, size),
                commentService.getCommentQueryWrapper(questionCommentQueryRequest.getQuestionId()).orderByDesc("createTime")); // 按照时间排序
        List<CommentVO> commentVOS = commentService.getCommentVOS(commentPage.getRecords(), request);
        Page<CommentVO> commentVOPage = new Page<>();
        commentVOPage.setRecords(commentVOS);
        commentVOPage.setTotal(commentVOS.size());
        return ResultUtils.success(commentVOPage);
    }

    /**
     * 点赞评论
     * @return 是否完成操作
     */
    @PutMapping("/comment/thumb")
    public BaseResponse<Boolean> thumbComment(Long commentId, HttpServletRequest request) {
        return ResultUtils.success(commentService.thumbComment(commentId, request));
    }

    /**
     * 取消点赞评论
     * @return 是否完成操作
     */
    @PutMapping("/comment/cancel/thumb")
    public BaseResponse<Boolean> cancelThumbComment(Long commentId, HttpServletRequest request) {
        return ResultUtils.success(commentService.cancelThumbComment(commentId, request));
    }

    /**
     * 添加评论
     * @return 评论id
     */
    @PostMapping("/comment/add")
    public BaseResponse<Long> addComment(@RequestBody QuestionCommentAddRequest questionCommentAddRequest, HttpServletRequest request) {
        // 查看该题目是否存在
        QuestionQueryRequest questionQueryRequest = new QuestionQueryRequest();
        questionQueryRequest.setId(questionCommentAddRequest.getQuestionId());
        QueryWrapper<Question> queryWrapper = questionService.getQueryWrapper(questionQueryRequest);
        Question question = questionService.getOne(queryWrapper);
        List<Question> list = questionService.list(queryWrapper);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (questionCommentAddRequest.getContent() == null || questionCommentAddRequest.getContent().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR); // content不能为空
        }
        QuestionComment questionComment = QuestionComment.builder().questionId(questionCommentAddRequest.getQuestionId())
                .content(questionCommentAddRequest.getContent())
                .userId(userFeignClient.getLoginUser(request).getId())
                .thumbNum(0)
                .build();
        boolean save = commentService.save(questionComment);
        if (save) {
            return ResultUtils.success(questionComment.getId());
        }else {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }

    /**
     * 删除评论
     * @return 是否完成操作
     */
    @DeleteMapping("/comment/delete")
    public BaseResponse<Boolean> deleteComment(Long commentId, HttpServletRequest request) {
        LambdaUpdateWrapper<QuestionComment> commentLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        commentLambdaUpdateWrapper.eq(QuestionComment::getId, commentId);
        commentLambdaUpdateWrapper.eq(QuestionComment::getUserId, userFeignClient.getLoginUser(request).getId());
        commentLambdaUpdateWrapper.set(QuestionComment::getIsDelete, 1);
        boolean update = commentService.update(commentLambdaUpdateWrapper);
        if (update) {
            return ResultUtils.success(true);
        }else {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }
}
