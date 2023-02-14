package com.wuwei.redisdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.wuwei.redisdemo.mapper")
public class RedisDemoApplication {



    public static void main(String[] args) {
        SpringApplication.run(RedisDemoApplication.class, args);


    }

}
