package ru.vych.http.impl.exceptions;

/**
 * Исключение выбрасываемое клиентом в случае если возникла ошибка во время обработки ответа
 */
public class HttpClientHandleResponseException extends HttpClientException {
    public HttpClientHandleResponseException(String message) {
        super(message);
    }

    public HttpClientHandleResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
