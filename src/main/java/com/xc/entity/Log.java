
package com.xc.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.springframework.ai.chat.messages.Message;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@TableName("ai_log")
public class Log extends BaseEntity  {

    private String chatId;

    private String question;

    private String response;

    private String messageType;

}