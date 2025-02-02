package com.whoj.whojbackendjudgeservice.judge.codeSandbox.impl;


import com.whoj.whojbackendjudgeservice.judge.codeSandbox.CodeSandbox;
import com.whoj.whojbackendjudgeservice.judge.codeSandbox.model.ExecuteCodeRequest;
import com.whoj.whojbackendjudgeservice.judge.codeSandbox.model.ExecuteCodeResponse;

/**
 * 第三方代码沙箱
 */
public class ThirdPartyCodeSandbox implements CodeSandbox {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("thirdParty");
        return null;
    }
}
