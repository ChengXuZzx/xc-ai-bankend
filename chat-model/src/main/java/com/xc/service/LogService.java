package com.xc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xc.entity.Log;

public interface LogService extends IService<Log> {

    void saveLog(String prompt, String chatId, String response);
}
