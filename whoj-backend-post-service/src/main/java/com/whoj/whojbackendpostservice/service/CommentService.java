package com.whoj.whojbackendpostservice.service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.whoj.whojbackendmodel.model.entity.PostComment;
import com.whoj.whojbackendmodel.model.vo.CommentVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CommentService extends IService<PostComment> {

    /**
     * 获取评论视图
     *
     * @param questionComments
     * @return
     */
    public List<CommentVO> getCommentVOS(List<PostComment> questionComments, HttpServletRequest request);

    public QueryWrapper<PostComment> getCommentQueryWrapper(Long questionId);

    public boolean thumbComment(Long commentId, HttpServletRequest request);

    public boolean cancelThumbComment(Long commentId, HttpServletRequest request);
}
