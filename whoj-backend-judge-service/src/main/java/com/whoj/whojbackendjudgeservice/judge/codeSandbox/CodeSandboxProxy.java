package com.whoj.whojbackendjudgeservice.judge.codeSandbox;

import com.whoj.whojbackendjudgeservice.judge.codeSandbox.model.ExecuteCodeRequest;
import com.whoj.whojbackendjudgeservice.judge.codeSandbox.model.ExecuteCodeResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CodeSandboxProxy implements CodeSandbox{

    private final CodeSandbox codeSandbox;

    public CodeSandboxProxy(CodeSandbox codeSandbox) {
        this.codeSandbox = codeSandbox;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("代码沙箱请求信息：" + executeCodeRequest.toString());
        ExecuteCodeResponse response = codeSandbox.executeCode(executeCodeRequest);
        log.info(("代码沙箱响应信息：" + response.toString()));
        return response;
    }
}
