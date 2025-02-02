package com.whoj.whojbackendpostservice.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whoj.whojbackcommon.common.BaseResponse;
import com.whoj.whojbackcommon.common.ErrorCode;
import com.whoj.whojbackcommon.common.ResultUtils;
import com.whoj.whojbackcommon.exception.BusinessException;
import com.whoj.whojbackendmodel.model.dto.comment.PostCommentQueryRequest;
import com.whoj.whojbackendmodel.model.dto.post.PostAddRequest;
import com.whoj.whojbackendmodel.model.dto.post.PostQueryRequest;
import com.whoj.whojbackendmodel.model.dto.comment.PostCommentAddRequest;
import com.whoj.whojbackendmodel.model.entity.Post;
import com.whoj.whojbackendmodel.model.entity.PostComment;
import com.whoj.whojbackendmodel.model.entity.User;
import com.whoj.whojbackendmodel.model.vo.CommentVO;
import com.whoj.whojbackendmodel.model.vo.PostGetVO;
import com.whoj.whojbackendmodel.model.vo.UserVO;
import com.whoj.whojbackendpostservice.service.CommentService;
import com.whoj.whojbackendpostservice.service.PostService;
import com.whoj.whojbackendserviceclient.service.UserFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/")
public class PostController {

    @Resource
    private PostService postService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private CommentService commentService;

    /**
     * 创建帖子
     * @param postAddRequest 参数
     * @param request 帖子id
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPost(@RequestBody PostAddRequest postAddRequest, HttpServletRequest request) {
        if (postAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = BeanUtil.copyProperties(postAddRequest, Post.class);
        postService.validPost(post);
        User loginUser = userFeignClient.getLoginUser(request);
        post.setUserId(loginUser.getId());
        post.setFavourNum(0);
        post.setThumbNum(0);
        post.setCommentNum(0);
        boolean save = postService.save(post);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加失败，请联系管理员！");
        }
        return ResultUtils.success(post.getId());
    }

    /**
     * 获取帖子
     * @param id 帖子id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<PostGetVO> getPost(@RequestParam Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = postService.getById(id);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        PostGetVO postGetVO = BeanUtil.copyProperties(post, PostGetVO.class);
        User user = userFeignClient.getById(post.getUserId());
        UserVO userVO = userFeignClient.getUserVO(user);
        postGetVO.setUserVO(userVO);
        postGetVO.setPreview(null);
        return ResultUtils.success(postGetVO);
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
    public BaseResponse<Long> addComment(@RequestBody PostCommentAddRequest postCommentAddRequest, HttpServletRequest request) {
        // 查看该帖子是否存在
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setId(postCommentAddRequest.getPostId());
        QueryWrapper<Post> queryWrapper = postService.getQueryWrapper(postQueryRequest);
        Post post = postService.getOne(queryWrapper);
        List<Post> list = postService.list(queryWrapper);
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
        LambdaUpdateWrapper<PostComment> commentLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        commentLambdaUpdateWrapper.eq(PostComment::getId, commentId);
        commentLambdaUpdateWrapper.eq(PostComment::getUserId, userFeignClient.getLoginUser(request).getId());
        commentLambdaUpdateWrapper.set(PostComment::getIsDelete, 1);
        boolean update = commentService.update(commentLambdaUpdateWrapper);
        if (update) {
            return ResultUtils.success(true);
        }else {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }

}