package com.whoj.whojbackenduserservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.whoj.whojbackenduserservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.whoj") // 用于扫描到 common 模块下的全局异常处理器
@EnableFeignClients(basePackages = {"com.whoj.whojbackendserviceclient.service"})
@EnableDiscoveryClient
public class WhojBackendUserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WhojBackendUserServiceApplication.class, args);
    }

}
