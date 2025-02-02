package com.whoj.whojbackendmodel.model.dto.question;

import lombok.Data;

/**
 * 题目用例
 */
@Data
public class JudgeConf {

    /**
     * 时间限制 (ms)
     */
    private Long timeLimit;

    /**
     * 空间限制 (kB)
     */
    private Long memoLimit;

    /**
     * 堆栈限制 (kB)
     */
    private Long stackLimit;
}
