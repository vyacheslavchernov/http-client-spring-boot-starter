package ru.vych.http.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import ru.vych.http.config.HttpClientConfig;

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

    @SneakyThrows
    public HttpClientImpl(HttpClientConfig config) {
        this.config = config;

        var clientBuilder = java.net.http.HttpClient.newBuilder()
                .connectTimeout(Duration.of(config.getTimeout(), MILLIS))
                .followRedirects(config.getAllowRedirects() ? ALWAYS : NEVER);

        if (config.getStoreCookies()) {
            clientBuilder.cookieHandler(config.getCookieHandlerClass().getConstructor().newInstance());
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
    @SneakyThrows
    public Response execute(Request request) {
        return switch (request.getMethod()) {
            case GET -> get(request);
            default -> new Response(request, 666, "Unknown http method " + request.getMethod(), null);
        };
    }

    @SneakyThrows
    private Response get(Request request) {
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

        var rs = client.send(rsBuilder.build(), HttpResponse.BodyHandlers.ofString());
        return new Response(
                request,
                rs.statusCode(),
                rs.body(),
                mapBodyToResponseClass(rs.body(), request.getResponseClass())
                );
    }

    @SneakyThrows
    private Object mapBodyToResponseClass(String body, Class<?> responseClass) {
        if (responseClass == String.class) {
            return body;
        }
        return mapper.readValue(body, responseClass);
    }
}
