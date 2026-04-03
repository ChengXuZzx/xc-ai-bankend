package com.xc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/honghong")
@RequiredArgsConstructor
public class HonghongController {

    private final ChatClient honghongChatClient;

    @RequestMapping("/chat")
    public String chat(@RequestParam String prompt,@RequestParam String chatId) {
        return honghongChatClient.prompt()
                .user(prompt)
                .advisors(a->a.param("chat_memory_conversation_id",chatId))
                .call()
                .content();
    }

    @RequestMapping(value = "/streamChat", produces = "text/html;charset=utf-8")
    public Flux<String> streamChat(@RequestParam String prompt,@RequestParam String chatId) {
        return honghongChatClient.prompt()
                .user(prompt)
                .advisors(a->a.param("chat_memory_conversation_id",chatId))
                .stream()
                .content();
    }
}
