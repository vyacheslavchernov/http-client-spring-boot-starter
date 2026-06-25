package ru.vych.http.impl;

import java.net.CookieHandler;

/**
 * Интерфейс клиента
 */
public interface HttpClient {
    /**
     * Выполнить http запрос
     *
     * @param request запрос для выполнения
     * @return результат выполнения запроса
     */
    Response execute(Request request);

    CookieHandler getCookieHandler();
}
