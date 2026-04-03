package com.xc.config;

import com.xc.repository.DatabaseChatMemoryRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PdfConfig implements WebMvcConfigurer {

    @Bean
    public ChatClient pdfChatClient(OpenAiChatModel model, ChatMemory chatMemory) {
        return ChatClient.builder(model)
                .defaultSystem("你是 PDF 文档分析助手，擅长解析和理解 PDF 文档内容。")
                .defaultAdvisors(new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @Bean
    public ChatMemory chatMemory(DatabaseChatMemoryRepository repository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(20)
                .build();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

}
