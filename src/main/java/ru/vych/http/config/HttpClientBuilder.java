package ru.vych.http.config;

import ru.vych.http.impl.HttpClient;
import ru.vych.http.impl.HttpClientImpl;
import ru.vych.http.impl.exceptions.HttpClientException;

/**
 * Билдер для http-клиента
 */
public class HttpClientBuilder {
    /**
     * @param config конфигурация клиента
     * @return http-клиент созданный на основе конфигурации
     */
    public HttpClient build(HttpClientConfig config) throws HttpClientException {
        return new HttpClientImpl(config);
    }
}
