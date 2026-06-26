package ru.vych.http.impl.exceptions;

public class HttpClientConfigurationException extends HttpClientException {
    public HttpClientConfigurationException(String message) {
        super(message);
    }

    public HttpClientConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
