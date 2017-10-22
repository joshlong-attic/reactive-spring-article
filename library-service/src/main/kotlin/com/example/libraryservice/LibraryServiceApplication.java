package com.example.libraryservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@SpringBootApplication
public class LibraryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryServiceApplication.class, args);
    }
}

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
class Book {
    @Id
    private String id;
    private String title;
}

interface BookRepository extends ReactiveMongoRepository<Book, String> {

    Flux<Book> findByTitle(String title);
}

@Slf4j
@Component
class SampleBookInitializer implements ApplicationRunner {

    private final BookRepository bookRepository;

    SampleBookInitializer(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // @formatter:off
        this.bookRepository
            .deleteAll()
            .thenMany(
                Flux.just("Professional Java Development with the Spring Framework",
                    "Cloud Native Java", "Spring Security 3.1", "Spring in Action"))
            .map(title -> new Book(null, title))
            .flatMap(this.bookRepository::save)
            .thenMany(this.bookRepository.findAll())
            .subscribe(book -> log.info(book.toString()));
        // @formatter:on
    }
}

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

    @GetMapping("/books/{id}")
    Mono<Book> byId(@PathVariable String id) {
        return this.bookRepository.findById(id);
    }

    
}