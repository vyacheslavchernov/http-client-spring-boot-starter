package ru.vych.http.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.vych.http.config.HttpClientConfig;
import ru.vych.http.impl.common.HttpStatus;
import ru.vych.http.impl.entities.Request;
import ru.vych.http.impl.entities.Response;
import ru.vych.http.impl.exceptions.HttpClientConfigurationException;
import ru.vych.http.impl.exceptions.HttpClientException;
import ru.vych.http.impl.exceptions.HttpClientExecuteRequestException;
import ru.vych.http.impl.exceptions.HttpClientHandleResponseException;

import java.lang.reflect.InvocationTargetException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.stream.Collectors;

import static java.net.http.HttpClient.Redirect.ALWAYS;
import static java.net.http.HttpClient.Redirect.NEVER;
import static java.time.temporal.ChronoUnit.MILLIS;

/**
 * Реализация http-клиента
 */
public class HttpClientImpl implements HttpClient {
    private final HttpClientConfig config;
    private final java.net.http.HttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public HttpClientImpl(HttpClientConfig config) throws HttpClientException {
        this.config = config;

        java.net.http.HttpClient.Builder clientBuilder;
        try {
            clientBuilder = java.net.http.HttpClient.newBuilder()
                    .connectTimeout(Duration.of(config.getTimeout(), MILLIS))
                    .followRedirects(config.getAllowRedirects() ? ALWAYS : NEVER)
                    .version(config.getVersion());
        } catch (IllegalArgumentException e) {
            throw new HttpClientConfigurationException("Некорректная конфигурация http-клиента", e);
        }

        if (config.getStoreCookies()) {
            try {
                clientBuilder.cookieHandler(config.getCookieHandlerClass().getConstructor().newInstance());
            } catch (InstantiationException | NoSuchMethodException |
                     InvocationTargetException | IllegalAccessException e) {
                throw new HttpClientConfigurationException("Не удалось создать экземпляр хранилища cookie", e);
            }
        }

        this.client = clientBuilder.build();

        CookieManager cookies = (CookieManager) this.client.cookieHandler().orElse(null);
        if (cookies != null) {
            config.getCookies().forEach((key, value) ->
                    cookies.getCookieStore().add(URI.create("*"), new HttpCookie(key, value))
            );
        }
    }

    @Override
    public CookieHandler getCookieHandler() {
        return this.client.cookieHandler().orElse(null);
    }

    @Override
    public Response execute(Request request) throws HttpClientException {
        return switch (request.getMethod()) {
            case GET -> get(request);
            case POST -> post(request);
        };
    }

    private Response get(Request request) throws HttpClientException {
        var requestBuilder = HttpRequest.newBuilder(buildUri(request));
        addHeaders(requestBuilder, request);
        requestBuilder.GET();

        HttpResponse<byte[]> rs;
        try {
            rs = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofByteArray());
        } catch (Exception e) {
            throw new HttpClientExecuteRequestException("Ошибка при отправке запроса", e);
        }
        return buildResponse(rs, request);
    }

    private Response post(Request request) throws HttpClientException {
        Builder requestBuilder = HttpRequest.newBuilder(buildUri(request));
        addHeaders(requestBuilder, request);
        requestBuilder.POST(buildBody(request));

        HttpResponse<byte[]> rs;
        try {
            rs = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofByteArray());
        } catch (Exception e) {
            throw new HttpClientExecuteRequestException("Ошибка при отправке запроса", e);
        }
        return buildResponse(rs, request);
    }

    private HttpRequest.BodyPublisher buildBody(Request request) throws HttpClientException {
        Object payload = request.getPayload();

        if (payload == null) {
            return HttpRequest.BodyPublishers.noBody();
        }

        try {
            if (payload instanceof String text) {
                return HttpRequest.BodyPublishers.ofString(text, StandardCharsets.UTF_8);
            }

            if (payload instanceof byte[] bytes) {
                return HttpRequest.BodyPublishers.ofByteArray(bytes);
            }

            return HttpRequest.BodyPublishers.ofString(
                    mapper.writeValueAsString(payload),
                    StandardCharsets.UTF_8
            );

        } catch (JsonProcessingException e) {
            throw new HttpClientHandleResponseException("Ошибка при обработке тела запроса", e);
        }
    }

    private URI buildUri(Request request) {
        var root = config.getRoot().endsWith("/") ? config.getRoot() : config.getRoot() + "/";

        var path = request.getUrl().startsWith("/") ? request.getUrl().substring(1) : request.getUrl();
        var pathParams = String.join("/", request.getPathParams());

        var queryParams = request.getQueryParams().entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        var fullPathBuilder = new StringBuilder(root);
        fullPathBuilder.append(path);
        if (!pathParams.isEmpty()) {
            fullPathBuilder.append("/");
            fullPathBuilder.append(pathParams);
        }
        if (!queryParams.isEmpty()) {
            fullPathBuilder.append("?");
            fullPathBuilder.append(queryParams);
        }
        return URI.create(fullPathBuilder.toString());
    }

    private void addHeaders(Builder builder, Request request) {
        config.getHeaders().forEach(builder::header);
        request.getHeaders().forEach(builder::header);
    }

    private Object mapBodyToResponseClass(String body, Class<?> responseClass) throws HttpClientException {
        if (responseClass == String.class) {
            return body;
        }

        if (responseClass == null || responseClass == byte.class || responseClass == byte[].class) {
            return null;
        }

        try {
            return mapper.readValue(body, responseClass);
        } catch (JsonProcessingException e) {
            throw new HttpClientHandleResponseException("Ошибка при обработке ответа", e);
        }
    }

    private Response buildResponse(HttpResponse<byte[]> httpResponse, Request request) throws HttpClientException {
        String bodyText = new String(httpResponse.body(), StandardCharsets.UTF_8);
        var rsType = request.getResponseClass();
        return new Response(
                request,
                httpResponse.statusCode(),
                httpResponse.body(),
                httpResponse.statusCode() != HttpStatus.OK || rsType != null && rsType != byte.class && rsType != byte[].class
                        ? bodyText
                        : null,
                httpResponse.statusCode() != HttpStatus.OK
                        ? null
                        : mapBodyToResponseClass(bodyText, request.getResponseClass())
        );
    }
}
