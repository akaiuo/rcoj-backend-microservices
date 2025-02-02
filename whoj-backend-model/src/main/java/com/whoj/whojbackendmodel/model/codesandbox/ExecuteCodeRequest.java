package com.whoj.whojbackendmodel.model.codesandbox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteCodeRequest {

    /**
     * 输入样例
     */
    @NotNull
    private List<String> inputList;

    /**
     * 程序代码
     */
    @NotNull
    private String code;

    /**
     * 程序语言
     */
    @NotNull
    private String lang;

    /**
     * 单个用例最大执行时间
     */
    private Long time;

}
