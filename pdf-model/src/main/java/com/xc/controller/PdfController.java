package com.xc.controller;

import com.xc.entity.Result;
import com.xc.repository.DatabaseChatMemoryRepository;
import com.xc.repository.FileRepository;
import com.xc.repository.LocalPdfFileRepostory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ai/pdf")
@RequiredArgsConstructor
public class PdfController {

    private final ChatClient pdfChatClient;

    private final VectorStore vectorStore;

    private final LocalPdfFileRepostory localPdfFileRepostory;

    private final FileRepository fileRepository;

    private final DatabaseChatMemoryRepository databaseChatMemoryRepository;

    @PostMapping("/upload")
    public Result uploadPdf(@RequestParam("file") MultipartFile file, @RequestParam String chatId) {
        try {
            String filename = file.getOriginalFilename();
            if (filename == null || !filename.toLowerCase().endsWith(".pdf")) {
                return Result.error("只支持 PDF 格式的文件");
            }
            localPdfFileRepostory.save(chatId, file.getResource());
            writeToVectorStore(file.getResource());
            return Result.ok("PDF上传成功，文件名：" + file.getOriginalFilename());
        }catch (Exception e){
            log.error("上传PDF文件失败", e);
            return Result.error("上传失败");
        }

    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadPdf(@RequestParam String chatId) throws UnsupportedEncodingException {
        Resource file = fileRepository.getFile(chatId);
        if (!file.exists()){
            return ResponseEntity.notFound().build();
        }
        String filename = URLEncoder.encode(file.getFilename(), "UTF-8");
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .body(file);
    }


    @RequestMapping(value = "/streamChat", produces = "text/html;charset=utf-8")
    public Flux<String> streamChat(@RequestParam String prompt, @RequestParam String chatId) {
        // 1. 从向量库检索相关文档
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder().query(prompt).topK(1).similarityThreshold(0.6d).build()
        );

        // 2. 构建增强提示词
        StringBuilder context = new StringBuilder();
        if (!documents.isEmpty()) {
            context.append("基于以下参考内容回答问题：\n\n");
            for (int i = 0; i < documents.size(); i++) {
                context.append("[").append(i + 1).append("] ")
                       .append(documents.get(i).getText())
                       .append("\n\n");
            }
            context.append("用户问题：").append(prompt);
        } else {
            context.append(prompt);
        }

        // 3. 调用 ChatClient
        return pdfChatClient.prompt()
                .user(context.toString())
                .advisors(a->a.param("chat_memory_conversation_id", chatId))
                .stream()
                .content();
    }

    @GetMapping("/getHistoryChatIds")
    public List<String> getHistory() {
        return databaseChatMemoryRepository.findConversationIds();
    }

    @GetMapping("/getChatMessagesByChatId")
    public List<Message> getChatMessagesByChatId(String chatId) {
        return databaseChatMemoryRepository.findByConversationId(chatId);
    }

    private void writeToVectorStore(Resource resource) {
        PagePdfDocumentReader reader = new PagePdfDocumentReader(resource, PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(ExtractedTextFormatter.defaults())
                .withPagesPerDocument(1).build()
        );
        List<Document> documents = reader.read();
        vectorStore.add(documents);
    }

}
