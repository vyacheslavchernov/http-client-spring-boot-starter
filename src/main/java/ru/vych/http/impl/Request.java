package ru.vych.http.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Класс запроса через http-клиент
 */
@Getter
@AllArgsConstructor
@Accessors(chain = true)
public class Request {
    private String url;
    private HttpMethod method;
    private Map<String, String> queryParams;
    private List<String> pathParams;
    private Map<String, String> headers;
    private Class<?> responseClass;

    public static Builder builder() {
        return new Builder();
    }

    @Setter
    @Accessors(chain = true)
    public static class Builder {
        private String url = "";
        private HttpMethod method;
        private Map<String, String> queryParams = new HashMap<>();
        private List<String> pathParams = new LinkedList<>();
        private Map<String, String> headers = new HashMap<>();
        private Class<?> responseClass;

        public Builder addQueryParam(String key, String value) {
            queryParams.put(key, value);
            return this;
        }

        public Builder addPathParam(String value) {
            pathParams.add(value);
            return this;
        }

        public Request build() {
            if (method == null) {
                throw new IllegalStateException("Для запроса необходимо указать используемый HTTP метод.");
            }

            if (responseClass == null) {
                throw new IllegalStateException("Для запроса необходимо указать класс ответа.");
            }

            return new Request(url, method, queryParams, pathParams, headers, responseClass);
        }
    }
}
