package com.whoj.whojbackendjudgeservice.judge.codeSandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.whoj.whojbackcommon.common.ErrorCode;
import com.whoj.whojbackcommon.exception.BusinessException;
import com.whoj.whojbackendjudgeservice.judge.codeSandbox.CodeSandbox;
import com.whoj.whojbackendjudgeservice.judge.codeSandbox.model.ExecuteCodeRequest;
import com.whoj.whojbackendjudgeservice.judge.codeSandbox.model.ExecuteCodeResponse;
import com.whoj.whojbackendmodel.model.enums.LangEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 远程代码沙箱
 */
@Component
public class RemoteCodeSandbox implements CodeSandbox {

    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEAD = "auth";

    @Value(value = "${codeSandbox.secret}")
    private String AUTH_REQUEST_SECRET = "secretKey";

    @Value(value = "${codeSandbox.url}")
    private String CODE_SANDBOX_URL = "192.168.136.131:8082"; // todo 获取不到配置文件中的配置

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        String url = CODE_SANDBOX_URL + "/executeCode";
        if (!executeCodeRequest.getLang().equals(LangEnum.CPP.getText()) || !executeCodeRequest.getLang().equals(LangEnum.C.getText())) {
            executeCodeRequest.setTime(executeCodeRequest.getTime() * 2); // 两倍给时
        }
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String resp = HttpUtil.createPost(url).body(json).header(AUTH_REQUEST_HEAD, AUTH_REQUEST_SECRET).execute().body();
        if (StringUtils.isBlank(resp)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "execute remote sandbox error: " + resp);
        }
        return JSONUtil.toBean(resp, ExecuteCodeResponse.class);
    }
}
