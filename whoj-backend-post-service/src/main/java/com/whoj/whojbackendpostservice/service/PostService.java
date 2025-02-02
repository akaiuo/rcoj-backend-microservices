package com.whoj.whojbackendpostservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.whoj.whojbackendmodel.model.dto.post.PostQueryRequest;
import com.whoj.whojbackendmodel.model.entity.Post;
import com.whoj.whojbackendmodel.model.vo.PostGetVO;

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

    /**
     * 获取帖子分页VO
     * @param postPage
     * @return
     */
    Page<PostGetVO> getPostSubmitVOPage(Page<Post> postPage);
}
