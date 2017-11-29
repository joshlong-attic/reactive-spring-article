package com.example.libraryservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;

@Profile("security")
@Configuration
@EnableWebFluxSecurity
class SecurityConfiguration {

    @Bean
    ReactiveUserDetailsService authentication() {
        User.UserBuilder builder = User.withDefaultPasswordEncoder();
        return new MapReactiveUserDetailsService(
                builder.username("rjohnson").password("pw").roles("ADMIN").build(),
                builder.username("cwalls").password("pw").roles().build(),
                builder.username("jlong").password("pw").roles().build(),
                builder.username("rwinch").password("pw").roles("ADMIN").build());
    }
    
    @Bean
    @Profile("authorization")
    SecurityWebFilterChain authorization(ServerHttpSecurity http) {
        ReactiveAuthorizationManager<AuthorizationContext> am = (auth, ctx) -> auth
            .map(authentication -> {
                Object author = ctx.getVariables().get("author");
                boolean matchesAuthor = authentication.getName().equals(author);
                boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(ga -> ga.getAuthority().contains("ROLE_ADMIN"));
                return (matchesAuthor || isAdmin);
            })
            .map(AuthorizationDecision::new);
        return http
                .httpBasic()
                .and()
                .authorizeExchange()
                  .pathMatchers("/books/{author}").access(am)
                  .anyExchange().hasRole("ADMIN")
                .and()
                .build();

    }


}
