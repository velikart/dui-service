# dui-service-client (library)

## Назначение
`dui-service-client` — библиотека с автоконфигурацией Spring Boot и сгенерированным WebClient-клиентом для вызова API `dui-service`.

## Как подключить

### В multi-module Gradle проекте

```gradle
api project(":dui-service-client")
```

### В внешнем проекте
Подключите артефакт из вашего Nexus/репозитория артефактов (координаты зависят от group/version вашей сборки).

## Конфигурация (переменные окружения)
Библиотека читает настройки с префиксом `dui-service.client`:

- `DUI_SERVICE_CLIENT_BASE_URL` (`dui-service.client.base-url`) — базовый URL сервиса, по умолчанию `http://localhost:8080`.
- `DUI_SERVICE_CLIENT_CONNECT_TIMEOUT_MS` (`dui-service.client.connect-timeout-ms`) — таймаут соединения, по умолчанию `2000`.
- `DUI_SERVICE_CLIENT_READ_TIMEOUT_MS` (`dui-service.client.read-timeout-ms`) — таймаут чтения, по умолчанию `5000`.

Если переменные не заданы, используются дефолтные значения.