package com.xc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xc.mapper.LogMapper;
import com.xc.entity.Log;
import com.xc.service.LogService;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements LogService {
    @Override
    public void saveLog(String prompt, String chatId, String response) {
        Log log = new Log();
        log.setChatId(chatId);
        log.setQuestion(prompt);
        log.setResponse(response);
        this.save(log);
    }
}
