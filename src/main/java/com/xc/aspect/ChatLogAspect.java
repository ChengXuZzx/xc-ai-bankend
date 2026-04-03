
package com.xc.aspect;

import com.xc.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Aspect
@Component
@Slf4j
//@RequiredArgsConstructor
public class ChatLogAspect {

    @Autowired
     LogService logService;

    @Pointcut("execution(* com.xc.controller.ChatController.streamChat(..))")
    public void chatPointcut() {
    }

    //@Around("chatPointcut()")
    public Flux<String> around(ProceedingJoinPoint joinPoint) throws Throwable {
        String prompt = (String) joinPoint.getArgs()[0];
        String chatId = (String) joinPoint.getArgs()[1];

        StringBuilder fullResponse = new StringBuilder();

        return ((Flux<String>) joinPoint.proceed())
                .doOnNext(chunk -> fullResponse.append(chunk))
                .doAfterTerminate(() -> {
                    try {
                        logService.saveLog(prompt, chatId, fullResponse.toString());
                    } catch (Exception e) {
                        log.error("保存日志失败：{}", e.getMessage());
                    }
                });
    }
}