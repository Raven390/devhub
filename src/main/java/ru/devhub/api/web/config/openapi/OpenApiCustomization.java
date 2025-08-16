package ru.devhub.api.web.config.openapi;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.customizers.PropertyCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Configuration
public class OpenApiCustomization {

    /**
     * Глобальные подсказки по типам свойств (UUID, даты/время, деньги, и т.д.).
     * ВАЖНО: всегда возвращаем property, а не null.
     */
    @Bean
    public PropertyCustomizer commonPropertyHints() {
        return (property, annotatedType) -> {
            Class<?> raw = rawClassOf(annotatedType.getType());

            if (raw == null) {
                return property;
            }

            // Коллекции: пытаемся проставить формат для items
            if (Collection.class.isAssignableFrom(raw) && property instanceof ArraySchema array) {
                Class<?> itemRaw = rawItemClassOf(annotatedType.getType());
                if (itemRaw == UUID.class) {
                    array.setItems(new StringSchema().format("uuid").example("3b7f2d78-8a66-4c0f-9d0a-0f2f1f3a6a11"));
                } else if (itemRaw == OffsetDateTime.class || itemRaw == Instant.class) {
                    array.setItems(new StringSchema().format("date-time").example("2025-08-09T10:00:00Z"));
                } else if (itemRaw == LocalDate.class) {
                    array.setItems(new StringSchema().format("date").example("2025-08-09"));
                }
                return array;
            }

            // Примитивные/простые типы
            if (raw == UUID.class) {
                property.setType("string");
                property.setFormat("uuid");
                property.setExample("3b7f2d78-8a66-4c0f-9d0a-0f2f1f3a6a11");
            } else if (raw == OffsetDateTime.class || raw == Instant.class) {
                property.setType("string");
                property.setFormat("date-time");
                property.setExample("2025-08-09T10:00:00Z");
            } else if (raw == LocalDate.class) {
                property.setType("string");
                property.setFormat("date");
                property.setExample("2025-08-09");
            } else if (raw == BigDecimal.class) {
                // Денежные/десятичные значения
                property.setType("number");
                property.setFormat("decimal");
                property.setExample("99.99");
            }

            return property;
        };
    }

    /**
     * Точечная правка components.schemas без аннотаций на DTO/моделях.
     * Пример: добавим описание/пример для CreateProjectResponse.
     */
    @Bean
    public OpenApiCustomizer tweakSchemas() {
        return openApi -> {
            if (openApi.getComponents() == null || openApi.getComponents().getSchemas() == null) return;
            Map<String, Schema> schemas = openApi.getComponents().getSchemas();

            Schema<?> resp = schemas.get("CreateProjectResponse");
            if (resp != null) {
                resp.setDescription("Ответ при создании проекта");
                // Пример верхнего уровня (Swagger UI покажет его как пример объекта)
                resp.setExample(Map.of("id", "3b7f2d78-8a66-4c0f-9d0a-0f2f1f3a6a11"));
            }

            // Пример: документация enum без аннотаций на самом enum
            Schema<?> status = schemas.get("status");
            if (status != null) {
                status.setDescription("""
                    Статус проекта:
                    - DRAFT — черновик
                    - RECRUITING — набор команды
                    - ACTIVE — работа идёт
                    - COMPLETED — завершён
                    - ARCHIVED — архив
                    """);
            }
        };
    }


    /**
     * Автотеги по пакету контроллера — чтобы не размечать аннотациями.
     */
    @Bean
    public OperationCustomizer tagByPackage() {
        return (operation, handlerMethod) -> {
            String pkg = handlerMethod.getBeanType().getPackageName();
            if (pkg.contains(".web.project")) {
                operation.addTagsItem("Projects");
            } else if (pkg.contains(".web.reference")) {
                operation.addTagsItem("Reference");
            }
            return operation;
        };
    }

    private static Class<?> rawClassOf(Type type) {
        if (type instanceof Class<?> c) return c;
        if (type instanceof ParameterizedType p) {
            Type raw = p.getRawType();
            return raw instanceof Class<?> c ? c : null;
        }
        return null;
    }

    private static Class<?> rawItemClassOf(Type type) {
        if (type instanceof ParameterizedType p) {
            Type[] args = p.getActualTypeArguments();
            if (args.length == 1) {
                Type arg = args[0];
                if (arg instanceof Class<?> c) return c;
                if (arg instanceof ParameterizedType ip) {
                    Type raw = ip.getRawType();
                    return raw instanceof Class<?> c ? c : null;
                }
            }
        }
        return null;
    }
}

