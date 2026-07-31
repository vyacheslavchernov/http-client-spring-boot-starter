package ru.vych.http.impl.interceptors;

import ru.vych.http.impl.HttpClient;
import ru.vych.http.impl.entities.Response;

/**
 * Интерфейс перехватчика для ответа.
 * Все перехватчики вызываются последовательно после того, как запрос был обработан и получен ответ.
 */
public interface ResponseInterceptor {
    /**
     * @param client   клиент, который выполнял запрос
     * @param response ответ для предварительной обработки
     */
    void handle(HttpClient client, Response response);
}
