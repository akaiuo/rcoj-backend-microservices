package com.whoj.whojbackendpostservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whoj.whojbackcommon.common.ErrorCode;
import com.whoj.whojbackcommon.exception.BusinessException;
import com.whoj.whojbackendmodel.model.entity.*;
import com.whoj.whojbackendmodel.model.vo.CommentVO;
import com.whoj.whojbackendpostservice.mapper.*;
import com.whoj.whojbackendpostservice.service.CommentService;
import com.whoj.whojbackendserviceclient.service.UserFeignClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, PostComment> implements CommentService {

    /**
     * 获取评论列表（VO）
     *
     * @param comments
     * @return
     */

    @Resource
    CommentThumbMapper commentThumbMapper;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private CommentMapper commentMapper;

    @Override
    public List<CommentVO> getCommentVOS(List<PostComment> postComments, HttpServletRequest request) {
        List<CommentVO> commentVOList = new ArrayList<>();
        User loginUser;
        try {
            loginUser = userFeignClient.getLoginUser(request);
        }catch (BusinessException ignored) {
            loginUser = null;
        }
        QueryWrapper<PostCommentThumb> queryWrapper = new QueryWrapper<>();
        for (PostComment postComment : postComments) {
            CommentVO commentVO = new CommentVO();
            BeanUtils.copyProperties(postComment, commentVO);
            // 查看当前登录用户是否点赞过该评论
            if (loginUser != null) {
                queryWrapper.eq("userId", loginUser.getId());
                queryWrapper.eq("commentId", postComment.getId());
                PostCommentThumb postCommentThumb = commentThumbMapper.selectOne(queryWrapper);
                if (postCommentThumb != null) {
                    commentVO.setHasThumb(1);
                }else {
                    commentVO.setHasThumb(0);
                }
            }
            // 当前评论的用户信息
            commentVO.setUserVO(userFeignClient.getUserVO(userFeignClient.getById(postComment.getUserId())));
            commentVOList.add(commentVO);
            queryWrapper.clear();
        }
         return commentVOList;
    }

    /**
     * 获取评论列表（VO）
     *
     * @param postId
     * @return
     */
    @Override
    public QueryWrapper<PostComment> getCommentQueryWrapper(Long postId) {
        QueryWrapper<PostComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("postId", postId);
        return queryWrapper;
    }


    /**
     * 点赞评论
     *
     * @param commentId
     * @param request
     * @return
     */
    @Override
    public boolean thumbComment(Long commentId, HttpServletRequest request) {
        QueryWrapper<PostCommentThumb> queryWrapper = new QueryWrapper<>();
        User loginUser = userFeignClient.getLoginUser(request);
        queryWrapper.eq("userId", loginUser.getId());
        queryWrapper.eq("commentId", commentId);
        PostCommentThumb postCommentThumb = commentThumbMapper.selectOne(queryWrapper);
        if (postCommentThumb != null) {
            return false;
        }else {
            postCommentThumb = PostCommentThumb.builder()
                   .userId(loginUser.getId())
                   .commentId(commentId)
                   .build();
            commentThumbMapper.insert(postCommentThumb);
            // 评论点赞数+1
            PostComment postComment = commentMapper.selectById(commentId);
            if (postComment == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
            }
            LambdaUpdateWrapper<PostComment> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                    lambdaUpdateWrapper.eq(PostComment::getId, commentId);
                    lambdaUpdateWrapper.set(PostComment::getThumbNum, postComment.getThumbNum() + 1);
            this.update(lambdaUpdateWrapper);
            return true;
        }
    }

    /**
     * 取消点赞评论
     *
     * @param commentId
     * @param request
     * @return
     */
    @Override
    public boolean cancelThumbComment(Long commentId, HttpServletRequest request) {
        QueryWrapper<PostCommentThumb> queryWrapper = new QueryWrapper<>();
        User loginUser = userFeignClient.getLoginUser(request);
        queryWrapper.eq("userId", loginUser.getId());
        queryWrapper.eq("commentId", commentId);
        PostCommentThumb postCommentThumb = commentThumbMapper.selectOne(queryWrapper);
        if (postCommentThumb == null) {
            return false;
        }else {
            commentThumbMapper.delete(queryWrapper);
            // 评论点赞数-1
            PostComment postComment = commentMapper.selectById(commentId);
            if (postComment == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
            }
            LambdaUpdateWrapper<PostComment> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(PostComment::getId, commentId);
            lambdaUpdateWrapper.set(PostComment::getThumbNum, postComment.getThumbNum() - 1);
            this.update(lambdaUpdateWrapper);
            return true;
        }
    }
}
