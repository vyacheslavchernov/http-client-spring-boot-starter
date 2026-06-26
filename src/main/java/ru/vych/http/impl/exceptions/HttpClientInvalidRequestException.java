package ru.vych.http.impl.exceptions;

/**
 * Исключение выбрасываемое в случае, когда запрос не соответствует требованиям клиента
 */
public class HttpClientInvalidRequestException extends HttpClientException {
    public HttpClientInvalidRequestException(String message) {
        super(message);
    }
}
