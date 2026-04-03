package com.xc.controller;

import com.xc.entity.ChatMessage;
import com.xc.service.repository.DatabaseChatMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class ChatController {

    private final ChatClient chatClient;

    private final DatabaseChatMemoryRepository databaseChatMemoryRepository;

    @RequestMapping("/chat")
    public String chat(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    @RequestMapping(value = "/streamChat", produces = "text/html;charset=utf-8")
    public Flux<String> streamChat(@RequestParam String prompt, @RequestParam String chatId) {
        return chatClient.prompt()
                .user(prompt)
                .advisors(a -> a.param("chat_memory_conversation_id", chatId))
                .stream()
                .content();
    }

    @GetMapping("/getHistoryChatIds")
    public List<String> getHistory() {
        return databaseChatMemoryRepository.findConversationIds();
    }

    @GetMapping("/getChatMessagesByChatId")
    public List<Message> getChatMessagesByChatId(String chatId) {
        return databaseChatMemoryRepository.findByConversationId(chatId);
    }
}
