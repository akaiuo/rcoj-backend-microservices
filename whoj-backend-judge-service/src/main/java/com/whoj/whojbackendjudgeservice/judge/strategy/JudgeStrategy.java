package com.whoj.whojbackendjudgeservice.judge.strategy;

/**
 * 判题策略
 */
public interface JudgeStrategy {
    /**
     * 执行判题
     * @param judgeContext
     * @return
     */

    JudgeContext doJudge(JudgeContext judgeContext);
}
