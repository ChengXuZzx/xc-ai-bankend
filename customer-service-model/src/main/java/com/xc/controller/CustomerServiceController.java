package com.xc.controller;

import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.alibaba.cloud.ai.graph.streaming.OutputType;
import com.alibaba.cloud.ai.graph.streaming.StreamingOutput;
import com.xc.service.impl.repository.DatabaseChatMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerServiceController {

    private final ChatClient customerServiceChatClient;

    private final ReactAgent customerServiceAgent;

    private final DatabaseChatMemoryRepository databaseChatMemoryRepository;

    @RequestMapping("/chat")
    public String chat(@RequestParam String prompt,@RequestParam String chatId) {
        return customerServiceChatClient.prompt()
                .user(prompt)
                .advisors(a->a.param("chat_memory_conversation_id",chatId))
                .call()
                .content();
    }

//    @RequestMapping(value = "/streamChat", produces = "text/html;charset=utf-8")
//    public Flux<String> streamChat(@RequestParam String prompt,@RequestParam String chatId) {
//        return customerServiceChatClient.prompt()
//                .user(prompt)
//                .advisors(a->a.param("chat_memory_conversation_id",chatId))
//                .stream()
//                .content();
//    }

    /**
     * 使用 Skills 机制的 AI 客服对话（支持天气查询等技能）
     */
    @RequestMapping("/chatWithSkills")
    public String chatWithSkills(@RequestParam String prompt, @RequestParam String chatId) throws GraphRunnerException {
        AssistantMessage response = customerServiceAgent.call(prompt);
        return response.getText();
    }

    /**
     * 使用 Skills 机制的 AI 客服对话 - 流式返回（支持天气查询等技能）
     */
    @RequestMapping(value = "/streamChat", produces = "text/html;charset=utf-8")
    public Flux<String> streamChatWithSkills(@RequestParam String prompt, @RequestParam String chatId) throws GraphRunnerException {
        Flux<NodeOutput> stream = customerServiceAgent.stream(prompt);
        // 收集所有输出
        List<NodeOutput> outputs = stream.collectList().block();
        NodeOutput last = outputs.getLast();
        Optional<Object> messages = last.state().value("messages");
        if (messages.isPresent()) {
            List<Message> messageList = (List<Message>) messages.get();
            Message lastMessage = messageList.get(messageList.size() - 1);
            if (lastMessage instanceof AssistantMessage assistantMsg) {
                return Flux.fromStream(assistantMsg.getText().chars().mapToObj(c -> String.valueOf((char) c)));
            }
        }
        return Flux.empty();
    }
}
