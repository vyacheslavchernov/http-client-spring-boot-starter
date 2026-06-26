package ru.vych.http.impl.exceptions;

/**
 * Корневое исключение клиента.
 * Все другие исключения должны наследоваться от него.
 */
public class HttpClientException extends Exception {
    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
