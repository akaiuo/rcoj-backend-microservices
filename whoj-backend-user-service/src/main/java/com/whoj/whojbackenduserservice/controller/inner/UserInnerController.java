package com.whoj.whojbackenduserservice.controller.inner;


import com.whoj.whojbackcommon.common.BaseResponse;
import com.whoj.whojbackcommon.common.ErrorCode;
import com.whoj.whojbackcommon.common.ResultUtils;
import com.whoj.whojbackcommon.exception.BusinessException;
import com.whoj.whojbackendmodel.model.entity.User;
import com.whoj.whojbackendmodel.model.vo.UserVO;
import com.whoj.whojbackendserviceclient.service.UserFeignClient;
import com.whoj.whojbackenduserservice.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 内部服务调用接口
 */
@RestController
@RequestMapping("/inner")
public class UserInnerController implements UserFeignClient {

    @Resource
    private UserService userService;

    /**
     * 根据id获取用户
     * @param userId
     * @return
     */
    @GetMapping("/get/id")
    @Override
    public User getById(@RequestParam("userId") Long userId) {
        return userService.getById(userId);
    }

    /**
     * 根据id获取用户列表
     * @param ids
     * @return
     */
    @GetMapping("/get/ids")
    @Override
    public List<User> listByIds(@RequestParam("ids") Collection<Long> ids) {
        return userService.listByIds(ids);
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param userName
     * @return
     */
    @GetMapping("/get/name")
    @Override
    public User getByName(@RequestParam("userName") String userName) {
        if (StringUtils.isBlank(userName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userService.lambdaQuery().eq(User::getUserName, userName).one();
    }
}
