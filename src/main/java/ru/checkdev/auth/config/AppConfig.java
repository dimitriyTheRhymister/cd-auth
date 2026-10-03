package ru.checkdev.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.checkdev.auth.util.CircuitBreaker;

@Configuration
public class AppConfig {

    @Bean
    public CircuitBreaker circuitBreaker() {
        return new CircuitBreaker(3); // 3 ошибки — открываем цепь
    }
}