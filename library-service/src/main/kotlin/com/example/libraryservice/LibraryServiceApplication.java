package com.example.libraryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibraryServiceApplication {

    public static void main(String[] args) {
        // todo
        System.setProperty("spring.profiles.active", "security,authorization,frp-java");
        SpringApplication.run(LibraryServiceApplication.class, args);
    }
}

