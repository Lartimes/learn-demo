package org.bankai.aop;


import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.bankai.mapper.UserActiveMapper;
import org.bankai.mapper.UserMapper;
import org.bankai.model.User;
import org.bankai.model.UserActive;
import org.bankai.service.SseManager;
import org.springframework.stereotype.Component;

import java.util.Date;

@Aspect
@Component
public class UserAspect {

    private final SseManager sseManager;
    private final UserActiveMapper userActiveMapper;
    private final UserMapper userMapper;

    public UserAspect(SseManager sseManager, UserActiveMapper userActiveMapper, UserMapper userMapper) {
        this.sseManager = sseManager;
        this.userActiveMapper = userActiveMapper;
        this.userMapper = userMapper;
    }

    @SneakyThrows
    @Around("execution(* org.bankai.controller.UserController.login(..))")
    public Object loginAspect(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Object proceed = joinPoint.proceed(args);
        try {
            if (args.length > 0 && args[0] instanceof User user) {
                Integer uid = user.getUid();
                sseManager.addConnection(String.valueOf(uid));
                Date date = new Date();
                user.setLoginTime(date);
                Integer ignore = userMapper.updateLogin(user);
                UserActive userActive = userActiveMapper.selectByUserId(uid);
                if (userActive == null) {
                    UserActive init = new UserActive();
                    init.setUserId(uid);
                    init.setLastActive(date);
                    userActiveMapper.insertInit(init);
                } else {
                    userActiveMapper.updateActive(uid, date);
                }
            }
        } catch (Exception ignore) {
            System.err.println("aspect error 登录");
        }
        return proceed;
    }


}
