package ru.devhub.web.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Конфигурация инфраструктурных компонентов приложения.
 * <p>
 * Определяет бины, используемые для интеграции с внешними системами и сервисами.
 * Обычно используется на уровне application/infrastructure для инкапсуляции технических зависимостей.
 * </p>
 */
@Configuration
public class WebConfig {

    /**
     * {@link RestTemplate} — синхронный HTTP-клиент для взаимодействия с внешними REST API.
     * <p>
     * Экземпляр создаётся как singleton-бин и может быть использован сервисами приложения для вызова внешних HTTP-сервисов,
     * например, при интеграции с OAuth2/Keycloak или сторонними API.
     * </p>
     *
     * <b>Важные замечания:</b>
     * <ul>
     *   <li>Рекомендуется настраивать таймауты, message converters, interceptors, если требуется кастомизация.</li>
     *   <li>Для высоконагруженных или асинхронных сценариев предпочтительнее использовать {@code WebClient}.</li>
     *   <li>Singleton RestTemplate потокобезопасен для большинства стандартных сценариев.</li>
     * </ul>
     *
     *
     * @return singleton-инстанс RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

