package com.xc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("chat_message")
public class ChatMessage extends BaseEntity {

    private String chatId;

    private String messageType;

    private String content;
    //对话类型。
    private String chatType;
}
