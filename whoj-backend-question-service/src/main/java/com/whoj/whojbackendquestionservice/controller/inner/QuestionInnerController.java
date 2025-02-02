package com.whoj.whojbackendquestionservice.controller.inner;

import com.whoj.whojbackendmodel.model.entity.Question;
import com.whoj.whojbackendmodel.model.entity.QuestionSubmit;
import com.whoj.whojbackendmodel.model.vo.CommentVO;
import com.whoj.whojbackendquestionservice.service.QuestionService;
import com.whoj.whojbackendquestionservice.service.QuestionSubmitService;
import com.whoj.whojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @GetMapping("/get/id")
    @Override
    public Question getQuestionById(@RequestParam("questionId") Long questionId) {
        return questionService.getById(questionId);
    }

    @GetMapping("/question_submit/get/id")
    @Override
    public QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") Long questionSubmitId) {
        return questionSubmitService.getById(questionSubmitId);
    }

    @PostMapping("/question_submit/update")
    @Override
    public boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit) {
        return questionSubmitService.updateById(questionSubmit);
    }
}
