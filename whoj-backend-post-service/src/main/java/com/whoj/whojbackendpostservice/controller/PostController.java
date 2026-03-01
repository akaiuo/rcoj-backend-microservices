package com.whoj.whojbackendpostservice.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whoj.whojbackcommon.common.BaseResponse;
import com.whoj.whojbackcommon.common.ErrorCode;
import com.whoj.whojbackcommon.common.ResultUtils;
import com.whoj.whojbackcommon.exception.BusinessException;
import com.whoj.whojbackendmodel.model.dto.comment.PostCommentQueryRequest;
import com.whoj.whojbackendmodel.model.dto.post.PostAddRequest;
import com.whoj.whojbackendmodel.model.dto.post.PostQueryRequest;
import com.whoj.whojbackendmodel.model.dto.comment.PostCommentAddRequest;
import com.whoj.whojbackendmodel.model.dto.post.SolutionPageQueryRequest;
import com.whoj.whojbackendmodel.model.entity.*;
import com.whoj.whojbackendmodel.model.enums.UserRoleEnum;
import com.whoj.whojbackendmodel.model.vo.CommentVO;
import com.whoj.whojbackendmodel.model.vo.PostGetVO;
import com.whoj.whojbackendpostservice.service.CommentService;
import com.whoj.whojbackendpostservice.service.PostService;
import com.whoj.whojbackendpostservice.service.SolutionService;
import com.whoj.whojbackendserviceclient.service.QuestionFeignClient;
import com.whoj.whojbackendserviceclient.service.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/")
public class PostController {

    @Resource
    private PostService postService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private CommentService commentService;

    @Resource
    private SolutionService solutionService;
    @Autowired
    private QuestionFeignClient questionFeignClient;

    /**
     * 创建帖子(questionId==null) / 题解(questionId!=null)
     * @param postAddRequest 参数，若questionId不为null则为题解
     * @param request 帖子id
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPost(@RequestBody PostAddRequest postAddRequest, HttpServletRequest request) {
        if (postAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (postAddRequest.getQuestionId() != null) {
            // 题目是否存在
            Question questionById = questionFeignClient.getQuestionById(postAddRequest.getQuestionId());
            if (questionById == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目未找到");
            }
        }

        Post post = BeanUtil.copyProperties(postAddRequest, Post.class);
        List<String> tags = postAddRequest.getTags();
        if (tags != null) {
            post.setTags(JSONUtil.toJsonStr(tags));
        }
        postService.validPost(post);
        User loginUser = userFeignClient.getLoginUser(request);
        post.setUserId(loginUser.getId());
        post.setFavourNum(0);
        post.setStarNum(0);
        post.setCommentNum(0);
        boolean save = postService.save(post); // 这行执行后post中的id变为数据库自增的id
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加失败，请联系管理员！");
        }

        if (postAddRequest.getQuestionId() != null) {
            QuestionSolutionPost solutionPost = QuestionSolutionPost.builder().postId(post.getId()).questionId(postAddRequest.getQuestionId()).build();
            boolean solutionSave = solutionService.save(solutionPost);
            if (!solutionSave) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加失败，请联系管理员！");
            }
        }

        return ResultUtils.success(post.getId());
    }

    /**
     * 获取帖子
     * @param id 帖子id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<PostGetVO> getPost(@RequestParam Long id, HttpServletRequest request) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = postService.getById(id);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(postService.getPostGetVO(post, request));
    }

    /**
     * 删除帖子（仅本人和管理员可删除）
     * @param id 删除帖子的id
     * @return
     */
    @DeleteMapping("/del")
    public BaseResponse<Long> delPost(@RequestParam Long id, HttpServletRequest request) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = postService.getById(id);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        if (!post.getUserId().equals(loginUser.getId()) && !loginUser.getUserRole().equals("admin")) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean removed = postService.removeById(id);
        if (!removed) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(post.getId());
    }

    /**
     * 分页获取帖子列表
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PostGetVO>> getPostList(@RequestBody PostQueryRequest postQueryRequest) {
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        Page<Post> page = postService.page(new Page<>(current, size), postService.getQueryWrapper(postQueryRequest));
        return ResultUtils.success(postService.getPostSubmitVOPage(page));
    }

    /**
     * 根据帖子id分页获取评论列表
     * @param postCommentQueryRequest
     * @param request
     * @return 查询出的评论分页列表
     */
    @PostMapping("/comment/list/page/vo")
    public BaseResponse<Page<CommentVO>> postCommentsByPostId(@RequestBody PostCommentQueryRequest postCommentQueryRequest, HttpServletRequest request) {
        long current = postCommentQueryRequest.getCurrent();
        long size = postCommentQueryRequest.getPageSize();
        Page<PostComment> commentPage = commentService.page(new Page<>(current, size),
                commentService.getCommentQueryWrapper(postCommentQueryRequest.getPostId()).orderByDesc("createTime")); // 按照时间排序
        List<CommentVO> commentVOS = commentService.getCommentVOS(commentPage.getRecords(), request);
        Page<CommentVO> commentVOPage = new Page<>();
        commentVOPage.setRecords(commentVOS);
        commentVOPage.setTotal(commentPage.getTotal());
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
    public BaseResponse<Long> addComment(@RequestBody PostCommentAddRequest postCommentAddRequest, HttpServletRequest request) {
        // 查看该帖子是否存在
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setId(postCommentAddRequest.getPostId());
        QueryWrapper<Post> queryWrapper = postService.getQueryWrapper(postQueryRequest);
        Post post = postService.getOne(queryWrapper);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (postCommentAddRequest.getContent() == null || postCommentAddRequest.getContent().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR); // content不能为空
        }
        PostComment postComment = PostComment.builder().postId(postCommentAddRequest.getPostId())
                .content(postCommentAddRequest.getContent())
                .userId(userFeignClient.getLoginUser(request).getId())
                .thumbNum(0)
                .replyNum(0)
                .build();
        boolean save = commentService.save(postComment);
        if (save) {
            // 帖子评论数+1
            post.setCommentNum(post.getCommentNum() + 1);
            postService.updateById(post);
            return ResultUtils.success(postComment.getId());
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
        PostComment comment = commentService.getById(commentId);
        User loginUser = userFeignClient.getLoginUser(request);
        if (Objects.equals(loginUser.getId(), comment.getUserId()) || loginUser.getUserRole().equals(UserRoleEnum.ADMIN.getText())) {
            // 可删除
            boolean del = commentService.removeById(comment);
            if (del) {
                // 帖子评论数-1
                Post post = postService.getById(comment.getPostId());
                post.setCommentNum(post.getCommentNum() - 1);
                postService.updateById(post);
                return ResultUtils.success(true);
            }else {
                throw new BusinessException(ErrorCode.OPERATION_ERROR);
            }
        }else {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
    }

    /**
     * 点赞帖子
     * @return 是否完成操作
     */
    @PutMapping("/favour")
    public BaseResponse<Boolean> favourPost(Long postId, HttpServletRequest request) {
        return ResultUtils.success(postService.favourPost(postId, request));
    }

    /**
     * 取消点赞帖子
     * @return 是否完成操作
     */
    @PutMapping("/cancel/favour")
    public BaseResponse<Boolean> cancelFavourPost(Long postId, HttpServletRequest request) {
        return ResultUtils.success(postService.cancelFavourPost(postId, request));
    }

    /**
     * 收藏帖子
     * @return 是否完成操作
     */
    @PutMapping("/star")
    public BaseResponse<Boolean> starPost(Long postId, HttpServletRequest request) {
        return ResultUtils.success(postService.starPost(postId, request));
    }

    /**
     * 取消收藏帖子
     * @return 是否完成操作
     */
    @PutMapping("/cancel/star")
    public BaseResponse<Boolean> cancelStarPost(Long postId, HttpServletRequest request) {
        return ResultUtils.success(postService.cancelStarPost(postId, request));
    }

    /**
     * 根据题目id列出题解
     * @param solutionPageQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/solution/list/page/vo")
    public BaseResponse<Page<PostGetVO>> getSolutionList(@RequestBody SolutionPageQueryRequest solutionPageQueryRequest, HttpServletRequest request) {
        Page<QuestionSolutionPost> questionSolutionPostPage = new Page<>(solutionPageQueryRequest.getCurrent(), solutionPageQueryRequest.getPageSize());
        Page<PostGetVO> pagePostVo = solutionService.getPagePostVo(solutionPageQueryRequest.getQuestionId(), questionSolutionPostPage);
        return ResultUtils.success(pagePostVo);
    }
}