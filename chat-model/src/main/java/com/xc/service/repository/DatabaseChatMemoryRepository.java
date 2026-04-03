package com.xc.service.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xc.constant.ChatType;
import com.xc.entity.ChatMessage;
import com.xc.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DatabaseChatMemoryRepository implements ChatMemoryRepository {

    private final ChatMessageService chatMessageService;

    @Override
    public List<String> findConversationIds() {
        return chatMessageService.list().stream()
                .map(ChatMessage::getChatId)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getChatId, conversationId).eq(ChatMessage::getChatType, ChatType.CHAT.getName())
                .orderByAsc(ChatMessage::getCreateTime);

        List<ChatMessage> messages = chatMessageService.list(wrapper);

        return messages.stream()
                .map(msg -> {
                    if ("USER".equals(msg.getMessageType())) {
                        return new UserMessage(msg.getContent());
                    } else {
                        return new AssistantMessage(msg.getContent());
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getChatId, conversationId);
        long existingCount = chatMessageService.count(wrapper);
        
        for (int i = (int) existingCount; i < messages.size(); i++) {
            Message msg = messages.get(i);
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setChatId(conversationId);
            chatMessage.setMessageType(msg.getMessageType() == MessageType.USER ? "USER" : "ASSISTANT");
            chatMessage.setContent(msg.getText());
            chatMessage.setChatType(ChatType.CHAT.getName());
            chatMessageService.save(chatMessage);
        }
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        chatMessageService.remove(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getChatId, conversationId));
    }
}
