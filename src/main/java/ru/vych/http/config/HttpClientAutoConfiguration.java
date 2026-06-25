package ru.vych.http.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Автоконфиг для билдера http-клиента
 */
@AutoConfiguration
public class HttpClientAutoConfiguration {
    @Bean
    public HttpClientBuilder httpClientBuilder() {
        return new HttpClientBuilder();
    }
}
