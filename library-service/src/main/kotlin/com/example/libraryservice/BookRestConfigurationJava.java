package com.example.libraryservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Profile("frp-java")
@Configuration
class BookRestConfigurationJava {

    //@formatter:off
    @Bean
    RouterFunction<?> routes(BookRepository br) {
        return
            route(GET("/books"),
                req -> ok().body(br.findAll(), Book.class))
            .andRoute(GET("/books/{author}"),
                req -> ok().body(br.findByAuthor(req.pathVariable("author")), Book.class));
    }
    //@formatter:on
}
