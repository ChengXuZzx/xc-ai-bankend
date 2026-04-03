package com.xc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ai_log")
public class Log extends BaseEntity {

    private String chatId;

    private String question;

    private String response;

    private String messageType;
}
