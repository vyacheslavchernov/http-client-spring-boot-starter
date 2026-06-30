package ru.vych.http.impl.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Класс ответа
 */
@AllArgsConstructor
@Getter
@ToString
public class Response {
    private final String uuid;
    private final Request request;
    private final Integer status;
    private final byte[] rawBytes;
    private final String rawBody;
    private final Object body;

    /**
     * Получить тело ответа кастованное в соответствующий ответу класс,
     * который был передан в запросе.
     *
     * @param <T> класс ответа
     * @return кастованное тело ответа
     */
    @JsonIgnore
    @SuppressWarnings("unchecked")
    public <T> T getCastedBody() {
        return (T) request.getResponseClass().cast(body);
    }
}
