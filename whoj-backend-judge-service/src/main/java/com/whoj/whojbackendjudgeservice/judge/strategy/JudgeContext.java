package com.whoj.whojbackendjudgeservice.judge.strategy;

import com.whoj.whojbackendmodel.model.codesandbox.JudgeInfo;
import com.whoj.whojbackendmodel.model.dto.question.JudgeCase;
import com.whoj.whojbackendmodel.model.dto.question.JudgeConf;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 上下文（用于定义在策略中传递的参数）
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JudgeContext {
    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;

    /**
     * 运行输出
     */
    private List<String> outputList;

    /**
     * 题目输入输出样例
     */
    private List<JudgeCase> judgeCaseList;

    /**
     * 题目限制
     */
    private JudgeConf JudgeConf;

    /**
     * 语言
     */
    private String lang;

    /**
     * 运行时间
     */
    private List<Long> timeList;

    /**
     * 运行消耗内存
     */
    private List<Long> memoList;

    /**
     * 判题信息列表
     */
    private List<String> messageList;

    /**
     * 错误输出
     */
    private List<String> errorOutputlist;
}
