package com.example.libraryservice;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public interface BookRepository extends ReactiveMongoRepository<Book, String> {

    Flux<Book> findByAuthor(String author);
}
