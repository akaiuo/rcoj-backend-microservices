package com.whoj.whojbackendvalidationservice.controller.inner;

import com.whoj.whojbackendmodel.model.dto.user.UserRegisterRequest;
import com.whoj.whojbackendserviceclient.service.ValidationFeignClient;
import com.whoj.whojbackendvalidationservice.service.RedisService;
import com.whoj.whojbackendvalidationservice.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/inner")
@Slf4j
public class ValidationController implements ValidationFeignClient {

    @Resource
    MailService mailService;

    @Resource
    RedisService redisService;

    @PostMapping("/registerSendCodeToMail")
    @Override
    public boolean registerSendCodeToMail(@RequestBody UserRegisterRequest userRegisterRequest) {
        String to = userRegisterRequest.getUserEmail();
        String sessionId = userRegisterRequest.getSessionId();
        if (StringUtils.isAnyBlank(to, sessionId)) {
            return false;
        }
        StringBuilder code = new StringBuilder();
        String num = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < 5; i++) {
            code.append(num.charAt((int) Math.floor(Math.random() * 34)));
        }
        try {
            mailService.sendTextMailMessage(to, "RCOJ注册", "您在RCOJ进行注册账号，验证码为：" + code.toString() + "，10分钟内有效!");
            redisService.set(sessionId + to, code.toString(), 600);
        }catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }

    @PostMapping("/validateCode")
    @Override
    public boolean validateCode(@RequestBody UserRegisterRequest userRegisterRequest) {
        String to = userRegisterRequest.getUserEmail();
        String code = userRegisterRequest.getValidateCode();
        String sessionId = userRegisterRequest.getSessionId();

        return code.equals(redisService.get(sessionId + to));
    }
}
