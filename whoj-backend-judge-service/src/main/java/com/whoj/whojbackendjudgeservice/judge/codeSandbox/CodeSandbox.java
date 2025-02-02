package com.whoj.whojbackendjudgeservice.judge.codeSandbox;


import com.whoj.whojbackendjudgeservice.judge.codeSandbox.model.ExecuteCodeRequest;
import com.whoj.whojbackendjudgeservice.judge.codeSandbox.model.ExecuteCodeResponse;

public interface CodeSandbox {

    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
