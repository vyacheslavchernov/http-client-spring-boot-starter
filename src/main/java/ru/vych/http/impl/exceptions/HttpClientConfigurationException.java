package ru.vych.http.impl.exceptions;

/**
 * Исключение выбрасываемое клиентом в случае если возникла во время обработки его конфигурации
 */
public class HttpClientConfigurationException extends HttpClientException {
    public HttpClientConfigurationException(String message) {
        super(message);
    }

    public HttpClientConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
