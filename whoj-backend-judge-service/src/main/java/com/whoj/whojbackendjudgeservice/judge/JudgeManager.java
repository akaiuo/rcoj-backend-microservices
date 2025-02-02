package com.whoj.whojbackendjudgeservice.judge;

import com.whoj.whojbackendjudgeservice.judge.strategy.CPPLangJudgeStrategy;
import com.whoj.whojbackendjudgeservice.judge.strategy.JudgeContext;
import com.whoj.whojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.whoj.whojbackendjudgeservice.judge.strategy.OtherLangJudgeStrategy;
import com.whoj.whojbackendmodel.model.enums.LangEnum;
import org.springframework.stereotype.Service;

@Service
public class JudgeManager {

    JudgeContext doJudge(JudgeContext judgeContext) {
        JudgeStrategy judgeStrategy;
        if (judgeContext.getLang().equals(LangEnum.C.getValue()) || judgeContext.getLang().equals(LangEnum.CPP.getValue())) {
            judgeStrategy = new CPPLangJudgeStrategy();
        }else {
            judgeStrategy = new OtherLangJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
