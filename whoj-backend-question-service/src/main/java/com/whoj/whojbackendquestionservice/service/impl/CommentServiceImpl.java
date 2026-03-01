package com.whoj.whojbackendquestionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whoj.whojbackcommon.common.ErrorCode;
import com.whoj.whojbackcommon.exception.BusinessException;
import com.whoj.whojbackendmodel.model.entity.QuestionComment;
import com.whoj.whojbackendmodel.model.entity.QuestionCommentThumb;
import com.whoj.whojbackendmodel.model.entity.User;
import com.whoj.whojbackendmodel.model.vo.CommentVO;
import com.whoj.whojbackendquestionservice.mapper.CommentMapper;
import com.whoj.whojbackendquestionservice.mapper.CommentThumbMapper;
import com.whoj.whojbackendquestionservice.service.CommentService;
import com.whoj.whojbackendserviceclient.service.UserFeignClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CommentServiceImpl extends ServiceImpl<CommentMapper, QuestionComment> implements CommentService {

    @Resource
    CommentThumbMapper commentThumbMapper;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private CommentMapper commentMapper;

    /**
     * 获取评论列表（VO）
     *
     * @param questionComments
     * @param request
     * @return
     */
    @Override
    public List<CommentVO> getCommentVOS(List<QuestionComment> questionComments, HttpServletRequest request) {
        List<CommentVO> commentVOList = new ArrayList<>();
        User loginUser;
        try {
            loginUser = userFeignClient.getLoginUser(request);
        }catch (BusinessException ignored) {
            loginUser = null;
        }
        QueryWrapper<QuestionCommentThumb> queryWrapper = new QueryWrapper<>();
        for (QuestionComment questionComment : questionComments) {
            CommentVO commentVO = new CommentVO();
            BeanUtils.copyProperties(questionComment, commentVO);
            // 查看当前登录用户是否点赞过该评论
            if (loginUser != null) {
                queryWrapper.eq("userId", loginUser.getId());
                queryWrapper.eq("commentId", questionComment.getId());
                QuestionCommentThumb questionCommentThumb = commentThumbMapper.selectOne(queryWrapper);
                if (questionCommentThumb != null) {
                    commentVO.setHasThumb(1);
                } else {
                    commentVO.setHasThumb(0);
                }
            }
            // 当前评论的用户信息
            commentVO.setUserVO(userFeignClient.getUserVO(userFeignClient.getById(questionComment.getUserId())));
            commentVOList.add(commentVO);
            queryWrapper.clear();
        }
         return commentVOList;
    }

    /**
     * 获取评论列表（VO）
     *
     * @param questionId
     * @return
     */
    @Override
    public QueryWrapper<QuestionComment> getCommentQueryWrapper(Long questionId) {
        QueryWrapper<QuestionComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("questionId", questionId);
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
        QueryWrapper<QuestionCommentThumb> queryWrapper = new QueryWrapper<>();
        User loginUser = userFeignClient.getLoginUser(request);
        queryWrapper.eq("userId", loginUser.getId());
        queryWrapper.eq("commentId", commentId);
        QuestionCommentThumb questionCommentThumb = commentThumbMapper.selectOne(queryWrapper);
        if (questionCommentThumb != null) {
            return false;
        }else {
            questionCommentThumb = QuestionCommentThumb.builder()
                   .userId(loginUser.getId())
                   .commentId(commentId)
                   .build();
            commentThumbMapper.insert(questionCommentThumb);
            // 评论点赞数+1
            QuestionComment questionComment = commentMapper.selectById(commentId);
            if (questionComment == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
            }
            LambdaUpdateWrapper<QuestionComment> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                    lambdaUpdateWrapper.eq(QuestionComment::getId, commentId);
                    lambdaUpdateWrapper.set(QuestionComment::getThumbNum, questionComment.getThumbNum() + 1);
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
        QueryWrapper<QuestionCommentThumb> queryWrapper = new QueryWrapper<>();
        User loginUser = userFeignClient.getLoginUser(request);
        queryWrapper.eq("userId", loginUser.getId());
        queryWrapper.eq("commentId", commentId);
        QuestionCommentThumb questionCommentThumb = commentThumbMapper.selectOne(queryWrapper);
        if (questionCommentThumb == null) {
            return false;
        }else {
            commentThumbMapper.delete(queryWrapper);
            // 评论点赞数-1
            QuestionComment questionComment = commentMapper.selectById(commentId);
            if (questionComment == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
            }
            LambdaUpdateWrapper<QuestionComment> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(QuestionComment::getId, commentId);
            lambdaUpdateWrapper.set(QuestionComment::getThumbNum, questionComment.getThumbNum() - 1);
            this.update(lambdaUpdateWrapper);
            return true;
        }
    }
}
