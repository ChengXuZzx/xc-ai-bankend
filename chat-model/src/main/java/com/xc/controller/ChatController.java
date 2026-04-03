package com.xc.controller;

import com.xc.entity.ChatMessage;
import com.xc.service.repository.DatabaseChatMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.content.Media;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

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

    @RequestMapping(value = "/multimodalChat", produces = "text/html;charset=utf-8")
    public Flux<String> multimodalChat(@RequestParam String prompt,
                                       @RequestParam String chatId,
                                       @RequestParam(required = false) List<MultipartFile> files) {
        if (null == files || files.isEmpty()) {
            return textChat(prompt, chatId);
        } else {
            return multimodalChatA(prompt, chatId, files);
        }
    }


    private Flux<String> textChat(String prompt, String chatId) {
        return chatClient.prompt()
                .user(prompt)
                .advisors(a -> a.param("chat_memory_conversation_id", chatId))
                .stream()
                .content();
    }


    private Flux<String> multimodalChatA(String prompt, String chatId, List<MultipartFile> files) {
        //1、解析多媒体
        List<Media> collect = files.stream().map(file -> new Media(MimeType.valueOf(file.getContentType()), file.getResource())).toList();
        return chatClient.prompt()
                .user(p -> p.text(prompt).media(collect.toArray(Media[]::new)))
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
