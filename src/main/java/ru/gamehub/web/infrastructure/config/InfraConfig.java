package ru.gamehub.web.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class InfraConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
