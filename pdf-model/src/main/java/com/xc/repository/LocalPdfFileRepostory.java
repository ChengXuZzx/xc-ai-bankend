package com.xc.repository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalPdfFileRepostory implements FileRepository {

    private final VectorStore vectorStore;

    //Properties 自带持久化存储的
    private final Properties chatFile = new Properties();

    @Override
    public boolean save(String conversationId, Resource resource) {
        String filename = resource.getFilename();
        File target = new File(Objects.requireNonNull(filename));
        if (!target.exists()) {
            try {
                Files.copy(resource.getInputStream(), target.toPath());
            } catch (Exception e) {
                log.error("save file error", e);
                return false;
            }
        }
        chatFile.put(conversationId, filename);
        return true;
    }

    @Override
    public Resource getFile(String chatId) {
        return new FileSystemResource(chatFile.getProperty(chatId));
    }

    @PostConstruct
    private void init(){
        FileSystemResource pdfResource = new FileSystemResource("chatFile.properties");
        if (pdfResource.exists()) {
            try {
                chatFile.load(new BufferedReader(new InputStreamReader(pdfResource.getInputStream())));
            }catch (Exception e){
                log.error("load file error", e);
            }
        }
    }

    @PreDestroy
    private void persist() {
        try {
            chatFile.store(new FileWriter("chatFile.properties"), String.valueOf(LocalDateTime.now()));
        }catch (Exception e){
            log.error("persist file error", e);
        }

    }
}
