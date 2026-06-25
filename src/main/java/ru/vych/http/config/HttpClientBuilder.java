package ru.vych.http.config;

import ru.vych.http.impl.HttpClient;
import ru.vych.http.impl.HttpClientImpl;

/**
 * Билдер для http-клиента
 */
public class HttpClientBuilder {
    /**
     * @param config конфигурация клиента
     * @return http-клиент созданный на основе конфигурации
     */
    public HttpClient build(HttpClientConfig config) {
        return new HttpClientImpl(config);
    }
}
