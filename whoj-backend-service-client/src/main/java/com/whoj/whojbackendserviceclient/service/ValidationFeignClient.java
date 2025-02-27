package com.whoj.whojbackendserviceclient.service;

import com.whoj.whojbackendmodel.model.dto.user.UserRegisterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 验证码服务
 */
@FeignClient(name = "whoj-backend-validation-service", path = "/api/validation/inner")
public interface ValidationFeignClient {

    /**
     * 发送验证码
     * @return 0 发送成功, 50010 发送失败
     */
    @PostMapping("/registerSendCodeToMail")
    boolean registerSendCodeToMail(@RequestBody UserRegisterRequest userRegisterRequest);

    /**
     * 校验验证码
     * @return 0 校验成功, 40500 校验失败
     */
    @PostMapping("/validateCode")
    boolean validateCode(@RequestBody UserRegisterRequest userRegisterRequest);
}
