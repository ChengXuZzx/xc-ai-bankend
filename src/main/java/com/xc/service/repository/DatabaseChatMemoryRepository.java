package com.xc.service.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xc.entity.ChatMessage;
import com.xc.entity.Log;
import com.xc.mapper.LogMapper;
import com.xc.service.ChatMessageService;
import com.xc.service.LogService;
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
        wrapper.eq(ChatMessage::getChatId, conversationId)
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
        // 查询该会话已保存的消息数量
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getChatId, conversationId);
        long existingCount = chatMessageService.count(wrapper);
        
        // 只保存新增的消息（从已有数量之后开始）
        for (int i = (int) existingCount; i < messages.size(); i++) {
            Message msg = messages.get(i);
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setChatId(conversationId);
            chatMessage.setMessageType(msg.getMessageType() == MessageType.USER ? "USER" : "ASSISTANT");
            chatMessage.setContent(msg.getText());
            chatMessageService.save(chatMessage);
        }
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        chatMessageService.remove(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getChatId, conversationId));
    }
}
