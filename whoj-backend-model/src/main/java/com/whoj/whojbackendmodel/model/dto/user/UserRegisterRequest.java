package com.whoj.whojbackendmodel.model.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 用户注册请求体
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRegisterRequest {

    private String userAccount;

    private String userPassword;

    private String userEmail;

    private String validateCode;

    private String sessionId;
}
