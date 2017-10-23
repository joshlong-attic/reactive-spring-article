package com.example.libraryservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.HttpSecurity;
import org.springframework.security.core.userdetails.MapUserDetailsRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Profile("security")
@Configuration
@EnableWebFluxSecurity
class SecurityConfiguration {

    @Bean
    MapUserDetailsRepository authentication() {
        return new MapUserDetailsRepository(
                user("rjohnson", "ADMIN"),
                user("cwalls"),
                user("jlong"),
                user("rwinch", "ADMIN"));
    }

    //@formatter:off
    @Bean
    @Profile("authorization")
    SecurityWebFilterChain authorization(HttpSecurity http) {
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

    private static UserDetails user(String u, String... roles) {
        List<String> r = new ArrayList<>(Arrays.asList(roles));
        r.add("USER");
        String[] rolesArray = r.toArray(new String[0]);
        return User.withUsername(u).password("pw").roles(rolesArray).build();
    }
}
