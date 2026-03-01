package com.whoj.whojbackendpostservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.whoj.whojbackendmodel.model.entity.QuestionSolutionPost;
import com.whoj.whojbackendmodel.model.vo.PostGetVO;

public interface SolutionService extends IService<QuestionSolutionPost> {

    public Page<PostGetVO> getPagePostVo(Long questionId, Page<QuestionSolutionPost> page);

}
