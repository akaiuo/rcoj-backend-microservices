package com.whoj.whojbackendmodel.model.dto.question;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 判题详细详细
 */
@Data
@Builder
public class JudgeInfoDetail {
    /**
     * 运行时间
     */
    private List<Long> timeList;

    /**
     * 运行消耗内存
     */
    private List<Long> memoList;

    /**
     * 判题结果
     */
    private List<String> messageList;

    /**
     * 错误输出
     */
    private List<String> errorOutputList;
}
