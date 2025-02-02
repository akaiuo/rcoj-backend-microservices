package com.whoj.whojbackendjudgeservice.judge.codeSandbox.impl;


import com.whoj.whojbackendjudgeservice.judge.codeSandbox.CodeSandbox;
import com.whoj.whojbackendjudgeservice.judge.codeSandbox.model.ExecuteCodeRequest;
import com.whoj.whojbackendjudgeservice.judge.codeSandbox.model.ExecuteCodeResponse;

/**
 * 示例代码沙箱
 */
public class ExampleCodeSandbox implements CodeSandbox {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("example code sandbox");
        return null;
    }
}
