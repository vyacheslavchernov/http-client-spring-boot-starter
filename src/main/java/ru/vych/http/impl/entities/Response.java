package ru.vych.http.impl.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Класс ответа
 */
@AllArgsConstructor
@Getter
@ToString
public class Response {
    private final String uuid;
    private final Request request;
    @Setter
    private Integer status;
    @Setter
    private byte[] rawBytes;
    @Setter
    private String rawBody;
    @Setter
    private Object body;
    @Setter
    private List<Header> headers;

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
