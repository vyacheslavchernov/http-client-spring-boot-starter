package ru.vych.http.impl.interceptors;

import ru.vych.http.impl.HttpClient;
import ru.vych.http.impl.entities.Request;

/**
 * Интерфейс перехватчика для запроса.
 * Все перехватчики вызываются последовательно до момента отправки запроса.
 */
public interface RequestInterceptor {
    /**
     * @param client  клиент, который будет выполнять запрос
     * @param request запрос для предварительной обработки
     */
    void handle(HttpClient client, Request request);
}
