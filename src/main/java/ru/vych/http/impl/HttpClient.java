package ru.vych.http.impl;

import ru.vych.http.impl.exceptions.HttpClientException;

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
    Response execute(Request request) throws HttpClientException;

    /**
     * Получить обработчик cookies клиента
     *
     * @return обработчик cookies
     */
    CookieHandler getCookieHandler();
}
