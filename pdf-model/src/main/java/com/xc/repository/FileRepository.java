package com.xc.repository;

import org.springframework.core.io.Resource;


public interface FileRepository {

    boolean save(String conversationId, Resource resource);


    Resource getFile(String chatId);
}
