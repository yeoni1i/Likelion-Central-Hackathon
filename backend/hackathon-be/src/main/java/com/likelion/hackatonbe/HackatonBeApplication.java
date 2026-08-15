package com.likelion.hackatonbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HackatonBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(HackatonBeApplication.class, args);
    }

}
