package com.xc.config;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.hook.skills.SkillsAgentHook;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.skills.registry.classpath.ClasspathSkillRegistry;
import com.alibaba.cloud.ai.graph.skills.registry.SkillRegistry;
import com.xc.constant.SystemConstant;
import com.xc.service.impl.repository.DatabaseChatMemoryRepository;
import com.xc.tools.CourseTools;
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

import java.util.List;
import java.util.Map;

@Configuration
public class CustomerServiceConfig implements WebMvcConfigurer {


    @Bean
    public ChatClient customerServiceChatClient(OpenAiChatModel model , ChatMemory chatMemory,CourseTools courseTools) {
        return ChatClient.builder(model)
                .defaultSystem(SystemConstant.CUSTOMER_SERVICE_SYSTEM_PROMPT)
                .defaultAdvisors(new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultTools(courseTools)
                .build();
    }

    /**
     * Skills 注册表：从 classpath:mySkills 加载技能
     */
    @Bean
    public SkillRegistry skillRegistry() {
        return ClasspathSkillRegistry.builder()
                .classpathPath("mySkills")
                .build();
    }

    /**
     * Skills Agent Hook：注册 read_skill 工具并注入技能列表
     */
    @Bean
    public SkillsAgentHook skillsAgentHook(SkillRegistry skillRegistry) {
        // 将 weather_query 工具与 weather-query skill 绑定，实现渐进式披露
//        Map<String, List<org.springframework.ai.tool.ToolCallback>> groupedTools = Map.of(
//                "weather-query",  // 与 SKILL.md 中的 name 一致
//                List.of(org.springframework.ai.tool.ToolCallback.of(weatherQueryTool))
//        );

        return SkillsAgentHook.builder()
                .skillRegistry(skillRegistry)
                //.groupedTools(groupedTools)
                .build();
    }

    /**
     * ReactAgent：使用 Skills 机制的 AI 客服代理
     */
    @Bean
    public ReactAgent customerServiceAgent(OpenAiChatModel model, 
                                           SkillsAgentHook skillsAgentHook,
                                           CourseTools courseTools) {
        return ReactAgent.builder()
                .name("customer-service-agent")
                .model(model)
                .systemPrompt(SystemConstant.CUSTOMER_SERVICE_SYSTEM_PROMPT)
                .saver(new MemorySaver())
                //.tools(List.of(courseTools))  // 基础工具
                .hooks(List.of(skillsAgentHook))  // Skills Hook
                .enableLogging(true)
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
