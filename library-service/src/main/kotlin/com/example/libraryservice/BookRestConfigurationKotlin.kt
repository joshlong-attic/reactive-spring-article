package com.example.libraryservice

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router

@Profile("frp-kotlin")
@Configuration
class BookRestConfigurationKotlin {

    //@formatter:off
    @Bean
    fun routes(br: BookRepository) = router {
        GET("/books") { r -> ok().body(br.findAll()) }
        GET("/books/{author}") { r -> ok().body(br.findByAuthor(r.pathVariable("author"))) }
    }
    //@formatter:on

}