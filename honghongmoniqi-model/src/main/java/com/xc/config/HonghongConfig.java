package com.xc.config;

import com.xc.constant.SystemConstant;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class HonghongConfig implements WebMvcConfigurer {

    @Bean
    public ChatClient gameChatClient(OpenAiChatModel model) {
        return ChatClient.builder(model)
                .defaultSystem(SystemConstant.GAME_SYSTEM_PROMPT)
                .defaultAdvisors(new SimpleLoggerAdvisor())
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
