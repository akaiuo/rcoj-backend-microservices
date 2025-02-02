package com.whoj.whojbackendjudgeservice.judge.codeSandbox.model;

import com.whoj.whojbackendmodel.model.codesandbox.JudgeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteCodeResponse {

    /**
     * 输出结果
     */
    private List<String> outputList;

    /**
     * 接口信息
     */
    private String message;

    /**
     * 执行信息 （废弃）
     */
    @Deprecated
    private JudgeInfo judgeInfo;

    /**
     * 执行状态
     */
    private Integer status;

    /**
     * 错误输出信息
     */
    private List<String> errorOutputList;

    /**
     * 执行时间
     */
    private List<Long> timeList;

    /**
     * 消耗内存
     */
    private List<Long> memoList;

    /**
     * 退出码
     */
    private List<Integer> exitValueList;

}
