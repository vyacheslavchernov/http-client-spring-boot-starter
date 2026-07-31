package ru.vych.http.config;

import ru.vych.http.impl.HttpClient;
import ru.vych.http.impl.HttpClientImpl;
import ru.vych.http.impl.exceptions.HttpClientException;
import ru.vych.http.impl.interceptors.RequestInterceptor;
import ru.vych.http.impl.interceptors.ResponseInterceptor;
import ru.vych.logger.impl.LogService;

import java.util.List;

/**
 * Билдер для http-клиента
 */
public class HttpClientBuilder {
    /**
     * @param config конфигурация клиента
     * @return http-клиент созданный на основе конфигурации
     */
    public HttpClient build(
            HttpClientConfig config, LogService logService,
            List<RequestInterceptor> requestInterceptors, List<ResponseInterceptor> responseInterceptors
    ) throws HttpClientException {
        return new HttpClientImpl(config, logService, requestInterceptors, responseInterceptors);
    }
}
