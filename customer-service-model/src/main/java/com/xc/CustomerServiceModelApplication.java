package com.xc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xc.mapper")
public class CustomerServiceModelApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceModelApplication.class, args);
    }

}
