package ru.vych.http.impl.exceptions;

public class HttpClientException extends Exception {
    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
