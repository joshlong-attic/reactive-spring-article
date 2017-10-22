package com.example.libraryservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.HttpSecurity;
import org.springframework.security.core.userdetails.MapUserDetailsRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
class SecurityConfiguration {

    @Bean
    MapUserDetailsRepository authentication() {

        return new MapUserDetailsRepository();
    }

    //@formatter:off
    @Bean
    SecurityWebFilterChain authorization (HttpSecurity http) {
        return
            http
            .httpBasic()
                .and()
            .authorizeExchange().anyExchange().hasRole("ADMIN")
                .and()
            .build();
    }
    //@formatter:off
}
