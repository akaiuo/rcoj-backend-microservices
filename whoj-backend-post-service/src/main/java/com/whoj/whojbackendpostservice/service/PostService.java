package com.whoj.whojbackendpostservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.whoj.whojbackendmodel.model.dto.post.PostQueryRequest;
import com.whoj.whojbackendmodel.model.entity.Post;
import com.whoj.whojbackendmodel.model.vo.PostGetVO;

import javax.servlet.http.HttpServletRequest;

public interface PostService extends IService<Post> {

    /**
     * 校验
     * @param post
     */
    void validPost(Post post);

    /**
     * 获取查询条件
     * @param postQueryRequest
     * @return
     */
    QueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest);

    PostGetVO getPostGetVO(Post post, HttpServletRequest request);

    /**
     * 获取帖子分页VO
     * @param postPage
     * @return
     */
    Page<PostGetVO> getPostSubmitVOPage(Page<Post> postPage);

    public boolean favourPost(Long postId, HttpServletRequest request);

    public boolean cancelFavourPost(Long postId, HttpServletRequest request);

    public boolean starPost(Long postId, HttpServletRequest request);

    public boolean cancelStarPost(Long postId, HttpServletRequest request);
}
