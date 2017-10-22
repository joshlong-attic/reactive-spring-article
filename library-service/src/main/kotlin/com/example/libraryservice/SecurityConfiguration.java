package com.example.libraryservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.HttpSecurity;
import org.springframework.security.core.userdetails.MapUserDetailsRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Profile("security")
@Configuration
@EnableWebFluxSecurity
class SecurityConfiguration {

    private static UserDetails user(String u, String... roles) {
        List<String> r = new ArrayList<>(Arrays.asList(roles));
        r.add("USER");
        String[] rolesArray = r.toArray(new String[0]);
        return User.withUsername(u).password("pw").roles(rolesArray).build();
    }

    @Bean
    MapUserDetailsRepository authentication() {
        return new MapUserDetailsRepository(
                user("jlong"),
                user("rjohnson", "ADMIN"),
                user("cwalls"),
                user("rwinch", "ADMIN"));
    }

    @Profile("authorization")
    @Bean
    SecurityWebFilterChain authorization(HttpSecurity http) {
        ReactiveAuthorizationManager<AuthorizationContext> authorizationManager = (auth, ctx) -> {
            Map<String, Object> vars = ctx.getVariables();
            Object author = vars.get("author");
            return auth
                    .map(a -> a.getName().equals(author))
                    .map(AuthorizationDecision::new);
        };
        return
                http
                        .httpBasic()
                        .and()
                        .authorizeExchange()
                        .pathMatchers("/books/{author}").access(authorizationManager)
                        .anyExchange().hasRole("ADMIN")
                        .and()
                        .build();
    }
}
