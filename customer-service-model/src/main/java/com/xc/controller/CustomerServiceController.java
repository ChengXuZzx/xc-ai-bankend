package com.xc.controller;

import com.xc.service.ChatMessageService;
import com.xc.service.impl.repository.DatabaseChatMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerServiceController {

    private final ChatClient customerServiceChatClient;

    private final DatabaseChatMemoryRepository databaseChatMemoryRepository;

    @RequestMapping("/chat")
    public String chat(@RequestParam String prompt,@RequestParam String chatId) {
        return customerServiceChatClient.prompt()
                .user(prompt)
                .advisors(a->a.param("chat_memory_conversation_id",chatId))
                .call()
                .content();
    }

    @RequestMapping(value = "/streamChat", produces = "text/html;charset=utf-8")
    public Flux<String> streamChat(@RequestParam String prompt,@RequestParam String chatId) {
        return customerServiceChatClient.prompt()
                .user(prompt)
                .advisors(a->a.param("chat_memory_conversation_id",chatId))
                .stream()
                .content();
    }
}
