package org.bankai.aop;


import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.bankai.model.User;
import org.bankai.service.SseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserAspect {

    @Autowired
    private SseManager sseManager;

    @SneakyThrows
    @Around("execution(* org.bankai.controller.UserController.login(..))")
    public Object loginAspect(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Object proceed = joinPoint.proceed(args);
        if (args.length > 0 && args[0] instanceof User user) {
            sseManager.addConnection(String.valueOf(user.getUid()));
        }
        return proceed;
    }


}
