package com.xc.config;

import com.xc.service.repository.DatabaseChatMemoryRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CommonConfiguration implements WebMvcConfigurer {

//    @Bean
//    public ChatClient chatClient(OllamaChatModel model, ChatMemory chatMemory) {
//        return ChatClient.builder(model)
//                .defaultSystem("你的名字叫小美，是个女孩子。")
//                .defaultAdvisors(new SimpleLoggerAdvisor(),
//                        MessageChatMemoryAdvisor.builder(chatMemory).build()
//                )
//                .build();
//    }

    //多模态聊天
    @Bean
    public ChatClient chatClient(OpenAiChatModel model, ChatMemory chatMemory) {
        return ChatClient.builder(model)
                .defaultOptions(ChatOptions.builder().model("qwen-omni-turbo").build())
                .defaultSystem("你的名字叫小美，是个女孩子。")
                .defaultAdvisors(new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
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
