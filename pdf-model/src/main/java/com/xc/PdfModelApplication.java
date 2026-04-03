package com.xc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xc.mapper")
public class PdfModelApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdfModelApplication.class, args);
    }

}
