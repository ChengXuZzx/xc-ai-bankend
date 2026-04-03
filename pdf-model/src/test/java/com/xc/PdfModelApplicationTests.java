package com.xc;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class PdfModelApplicationTests {

    @Autowired
    OpenAiEmbeddingModel openAiEmbeddingModel;

    @Autowired
    VectorStore vectorStore;
    @Test
    void contextLoads() {
        float[] floats = openAiEmbeddingModel.embed("我想学些 AI 应用的开发");
        System.out.println(Arrays.toString(floats));
    }

    @Test
    public void testVectorStore(){
        PagePdfDocumentReader reader = new PagePdfDocumentReader(new FileSystemResource("C:\\Data\\xcDev\\xc.pdf"),
                PdfDocumentReaderConfig.builder()
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.defaults())
                        .withPagesPerDocument(1)
                        .build());

        List<Document> documents = reader.read();
        vectorStore.add(documents);

        SearchRequest searchRequest = SearchRequest.builder()
                .query("徐成的年龄？")
                .topK(1)
                .similarityThreshold(0.5)
                .build();

        List<Document> docs = vectorStore.similaritySearch(searchRequest);

        if (docs == null){
            System.out.println("暂无内容");
        }
        docs.forEach(doc -> System.out.println(doc.getScore() + " : " + doc.getText()));
    }


}
