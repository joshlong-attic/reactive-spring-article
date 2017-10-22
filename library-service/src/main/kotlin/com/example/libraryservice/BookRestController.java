package com.example.libraryservice;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Profile("mvc-style")
@RestController
class BookRestController {

    private final BookRepository bookRepository;

    BookRestController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping("/books")
    Flux<Book> all() {
        return this.bookRepository.findAll();
    }

    @GetMapping("/books/{author}")
    Flux<Book> byAuthor(@PathVariable String author) {
        return this.bookRepository.findByAuthor(author);
    }
}
