package com.example.libraryclient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class LibraryClientApplication {

    @Bean
    WebClient webClient(@Value("${libary-service:http://localhost:8080/}") String url) {
        ExchangeFilterFunction basicAuthentication = ExchangeFilterFunctions
                .basicAuthentication("rwinch", "pw");
        return WebClient.builder().baseUrl(url).filter(basicAuthentication).build();
    }

    @Bean
    ApplicationRunner run(WebClient client) {
        //@formatter:off
        return args ->
            client
                .get()
                .uri("/books")
                .retrieve()
                .bodyToFlux(Book.class)
                .subscribe(System.out::println);
        //@formatter:on
    }

    public static void main(String[] args) {
        SpringApplication.run(LibraryClientApplication.class, args);
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Book {
    private String id;
    private String title;
    private String author;
}
