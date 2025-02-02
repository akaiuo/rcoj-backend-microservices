package com.whoj.whojbackendjudgeservice.judge.codeSandbox;


import com.whoj.whojbackendjudgeservice.judge.codeSandbox.impl.ExampleCodeSandbox;
import com.whoj.whojbackendjudgeservice.judge.codeSandbox.impl.RemoteCodeSandbox;
import com.whoj.whojbackendjudgeservice.judge.codeSandbox.impl.ThirdPartyCodeSandbox;

public class CodeSandboxFactory {
    public static CodeSandbox newInstance(String type) {
        return switch (type) {
            case "remote" -> new RemoteCodeSandbox();
            case "thirdParty" -> new ThirdPartyCodeSandbox();
            default -> new ExampleCodeSandbox();
        };
    }
}
