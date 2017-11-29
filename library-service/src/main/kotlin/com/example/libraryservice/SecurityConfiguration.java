package com.example.libraryservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Profile("security")
@Configuration
@EnableWebFluxSecurity
class SecurityConfiguration {

    @Bean
    ReactiveUserDetailsService authentication() {
        return new MapReactiveUserDetailsService(
                User.withDefaultPasswordEncoder().username("rjohnson").password("pw").roles("ADMIN").build(),
                User.withDefaultPasswordEncoder().username("cwalls").password("pw").roles().build(),
                User.withDefaultPasswordEncoder().username("jlong").password("pw").roles().build(),
                User.withDefaultPasswordEncoder().username("rwinch").password("pw").roles("ADMIN").build());
    }

    //@formatter:off
    @Bean
    @Profile("authorization")
    SecurityWebFilterChain authorization(ServerHttpSecurity http) {
        return
                http
                        .httpBasic()
                        .and()
                        .authorizeExchange()
                        .pathMatchers("/books/{author}").access((auth, ctx) ->
                        auth
                                .map(authentication -> {
                                    Object author = ctx.getVariables().get("author");
                                    boolean matchesAuthor = authentication.getName().equals(author);
                                    boolean isAdmin = authentication.getAuthorities().stream()
                                            .anyMatch(ga -> ga.getAuthority().contains("ROLE_ADMIN"));
                                    return (matchesAuthor || isAdmin);
                                })
                                .map(AuthorizationDecision::new)
                )
                        .anyExchange().hasRole("ADMIN")
                        .and()
                        .build();
    }
    //@formatter:on

}
