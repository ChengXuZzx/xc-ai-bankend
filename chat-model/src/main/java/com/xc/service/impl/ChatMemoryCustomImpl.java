package com.xc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xc.entity.Log;
import com.xc.mapper.LogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ChatMemoryCustomImpl implements ChatMemory {

    private final LogMapper logMapper;

    @Override
    public void add(String conversationId, List<Message> messages) {
        for (Message message : messages) {
            Log log = new Log();
            log.setChatId(conversationId);
            log.setQuestion(message.getText());
            log.setMessageType(message.getMessageType() == MessageType.USER ? "user" : "assistant");
            logMapper.insert(log);
        }
    }

    @Override
    public List<Message> get(String conversationId) {
        LambdaQueryWrapper<Log> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Log::getChatId, conversationId)
                .orderByAsc(Log::getCreateTime)
                .last("LIMIT 10");
        List<Log> logs = logMapper.selectList(wrapper);
        
        List<Message> messages = new ArrayList<>();
        for (Log log : logs) {
            if ("user".equals(log.getMessageType())) {
                messages.add(new UserMessage(log.getQuestion()));
            } else {
                messages.add(new AssistantMessage(log.getQuestion()));
            }
        }
        return messages;
    }

    @Override
    public void clear(String conversationId) {
        LambdaQueryWrapper<Log> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Log::getChatId, conversationId);
        logMapper.delete(wrapper);
    }
}
