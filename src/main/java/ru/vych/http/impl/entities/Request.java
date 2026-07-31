package ru.vych.http.impl.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.vych.http.impl.common.HttpMethod;
import ru.vych.http.impl.exceptions.HttpClientInvalidRequestException;

import java.util.*;

/**
 * Класс запроса через http-клиент
 */
@Getter
@AllArgsConstructor
@Accessors(chain = true)
@ToString
public class Request {
    private final String uuid = UUID.randomUUID().toString();
    @Setter
    private String url;
    @Setter
    private HttpMethod method;
    @Setter
    private Map<String, String> queryParams;
    @Setter
    private List<String> pathParams;
    @Setter
    private List<Header> headers;
    @Setter
    private Class<?> responseClass;
    @Setter
    private Object payload;

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
        private List<Header> headers = new LinkedList<>();
        private Class<?> responseClass;
        private Object payload;

        private String contentType;

        public Builder setQueryParams(Map<String, String> params) {
            params.remove(null);
            this.queryParams = params;
            return this;
        }

        public Builder addQueryParam(String key, String value) {
            if (key != null) {
                queryParams.put(key, value);
            }
            return this;
        }

        public Builder addPathParam(String value) {
            pathParams.add(value);
            return this;
        }

        public Builder addHeader(String name, String value) {
            headers.add(new Header(name, value));
            return this;
        }

        public Request build() throws HttpClientInvalidRequestException {
            if (method == null) {
                throw new HttpClientInvalidRequestException("Для запроса необходимо указать используемый HTTP метод.");
            }

            if ((method == HttpMethod.POST && payload != null) && (contentType == null || contentType.isEmpty())) {
                throw new HttpClientInvalidRequestException("Для POST запроса необходимо указать тип передаваемого контента.");
            }

            if (contentType != null) {
                this.addHeader("Content-Type", contentType);
            }

            return new Request(
                    url, method, queryParams,
                    pathParams, headers, responseClass,
                    payload
            );
        }
    }
}
