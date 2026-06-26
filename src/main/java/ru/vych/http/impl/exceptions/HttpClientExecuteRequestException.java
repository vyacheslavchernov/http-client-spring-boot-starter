package ru.vych.http.impl.exceptions;

/**
 * Исключение выбрасываемое клиентом в случае если возникла ошибка во время отправки запроса
 */
public class HttpClientExecuteRequestException extends HttpClientException {
    public HttpClientExecuteRequestException(String message) {
        super(message);
    }

    public HttpClientExecuteRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
