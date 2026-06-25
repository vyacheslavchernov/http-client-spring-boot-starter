package ru.vych.http.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Класс ответа
 */
@AllArgsConstructor
@Getter
public class Response {
    private final Request request;
    private final Integer status;
    private final String rawBody;
    private final Object body;

    @SuppressWarnings("unchecked")
    public <T> T getCastedBody() {
        return (T) request.getResponseClass().cast(body);
    }
}
