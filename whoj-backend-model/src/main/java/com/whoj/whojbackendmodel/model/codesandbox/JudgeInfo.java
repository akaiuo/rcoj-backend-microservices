package com.whoj.whojbackendmodel.model.codesandbox;

import lombok.Builder;
import lombok.Data;

/**
 * 题目用例
 */
@Builder
@Data
public class JudgeInfo {

    /**
     * 消耗时间 (ms)
     */
    private Long maxTime;

    /**
     * 消耗空间 (kB)
     */
    private Long maxMemo;

    /**
     * 题目总判题结果
     */
    private String message;

}
