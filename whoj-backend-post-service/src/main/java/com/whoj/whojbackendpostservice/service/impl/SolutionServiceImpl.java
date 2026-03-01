package com.whoj.whojbackendpostservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whoj.whojbackendmodel.model.entity.QuestionSolutionPost;
import com.whoj.whojbackendmodel.model.vo.PostGetVO;
import com.whoj.whojbackendpostservice.mapper.SolutionPostMapper;
import com.whoj.whojbackendpostservice.service.PostService;
import com.whoj.whojbackendpostservice.service.SolutionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SolutionServiceImpl extends ServiceImpl<SolutionPostMapper, QuestionSolutionPost> implements SolutionService {

    @Resource
    private SolutionPostMapper solutionPostMapper;

    @Resource
    private PostService postService;

    @Override
    public Page<PostGetVO> getPagePostVo(Long questionId, Page<QuestionSolutionPost> page) {
        LambdaQueryWrapper<QuestionSolutionPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(QuestionSolutionPost::getQuestionId, questionId);
        Page<QuestionSolutionPost> questionSolutionPostPage = solutionPostMapper.selectPage(page, queryWrapper); // 查到分页的帖子对应
        return postService.getPostSolutionVOPage(questionSolutionPostPage);
    }
}
