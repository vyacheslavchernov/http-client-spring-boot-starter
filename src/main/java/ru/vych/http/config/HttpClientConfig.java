package ru.vych.http.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация http-клиента
 */
@Getter
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
public class HttpClientConfig {
    /**
     * Сервис-код клиента
     */
    private final String serviceCode;

    /**
     * Корневая точка для клиента.
     * От неё будут производиться все запросы.
     */
    private String root = "";

    /**
     * Тайм-аут запросов клиента
     */
    private Integer timeout = 15000;

    /**
     * Заголовки, которые всегда добавляются к запросам клиента
     */
    private Map<String, String> headers = new HashMap<>();

    /**
     * Куки, которые всегда добавляются к запросам клиента
     */
    private Map<String, String> cookies = new HashMap<>();

    /**
     * Следует ли сохранять куки из полученных ответов
     */
    private Boolean storeCookies = false;

    /**
     * Какой обработчик кук будет использоваться клиентом
     */
    private Class<? extends CookieHandler> cookieHandlerClass = CookieManager.class;

    /**
     * Следует ли переходить по полученным редиректам
     */
    private Boolean allowRedirects = false;

    /**
     * Версия протокола http, которая будет использовать там, где это возможно
     */
    private HttpClient.Version version = HttpClient.Version.HTTP_1_1;
}
