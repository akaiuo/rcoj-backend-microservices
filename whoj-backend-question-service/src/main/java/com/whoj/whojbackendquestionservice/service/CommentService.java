package com.whoj.whojbackendquestionservice.service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.whoj.whojbackendmodel.model.entity.QuestionComment;
import com.whoj.whojbackendmodel.model.vo.CommentVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CommentService extends IService<QuestionComment> {

    /**
     * 获取评论视图
     *
     * @param questionComments
     * @return
     */
    public List<CommentVO> getCommentVOS(List<QuestionComment> questionComments, HttpServletRequest request);

    public QueryWrapper<QuestionComment> getCommentQueryWrapper(Long questionId);

    public boolean thumbComment(Long commentId, HttpServletRequest request);

    public boolean cancelThumbComment(Long commentId, HttpServletRequest request);
}
