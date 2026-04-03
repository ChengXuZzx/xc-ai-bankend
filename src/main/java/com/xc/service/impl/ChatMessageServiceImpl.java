package com.xc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xc.entity.ChatMessage;
import com.xc.mapper.ChatMessageMapper;
import com.xc.service.ChatMessageService;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {
}
