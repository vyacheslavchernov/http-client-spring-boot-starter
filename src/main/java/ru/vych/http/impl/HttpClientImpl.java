package ru.vych.http.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import ru.vych.http.config.HttpClientConfig;
import ru.vych.http.impl.exceptions.HttpClientConfigurationException;
import ru.vych.http.impl.exceptions.HttpClientException;
import ru.vych.http.impl.exceptions.HttpClientInvalidRequestException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

        var clientBuilder = java.net.http.HttpClient.newBuilder()
                .connectTimeout(Duration.of(config.getTimeout(), MILLIS))
                .followRedirects(config.getAllowRedirects() ? ALWAYS : NEVER);

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
            default -> throw new HttpClientInvalidRequestException("Неизвестный http-метод + request.getMethod()");
        };
    }

    private Response get(Request request) throws HttpClientException {
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

        var rsBuilder = HttpRequest.newBuilder(URI.create(fullPathBuilder.toString())).GET();

        config.getHeaders().forEach(rsBuilder::header);
        request.getHeaders().forEach(rsBuilder::header);

        HttpResponse<String> rs;
        try {
            rs = client.send(rsBuilder.build(), HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new HttpClientException("Ошибка при отправке запроса", e);
        }
        return new Response(
                request,
                rs.statusCode(),
                rs.body(),
                mapBodyToResponseClass(rs.body(), request.getResponseClass())
                );
    }

    private Object mapBodyToResponseClass(String body, Class<?> responseClass) throws HttpClientException {
        if (responseClass == String.class) {
            return body;
        }
        try {
            return mapper.readValue(body, responseClass);
        } catch (JsonProcessingException e) {
            throw new HttpClientException("Ошибка при обработке ответа", e);
        }
    }
}
