# dui-service (service)

## Назначение
`dui-service` — Spring Boot сервис, который предоставляет REST API для управления коллекциями, страницами и шаблонами Dynamic UI, а также MCP-инструменты для работы с этими сущностями.

## MCP (Model Context Protocol)

### Что реализовано
В сервисе поднят MCP Server на базе Spring AI (`spring-ai-starter-mcp-server-webmvc`).
Он автоматически регистрирует инструменты из классов, помеченных `@DuiMcpTool`.

Текущие MCP-группы инструментов:

- `CollectionMcpTool`
    - `listCollections`
    - `getCollectionManifest`
    - `saveCollectionManifest`
    - `createCollectionManifest`
- `PageMcpTool`
    - `listPages`
    - `getPageInstructions`
- `TemplateMcpTool`
    - `listTemplates`
    - `getTemplatePageJson`
    - `getTemplateImageName`
- `DocumentationMcpTool`
    - `listDocumentationFiles`
    - `getDocumentationFile`

### Конфигурация MCP
Базовые настройки MCP задаются в `service/src/main/resources/config/application.yml`:

- `spring.ai.mcp.server.transport=WEBMVC`
- `spring.ai.mcp.server.name=dui-collection-mcp`
- `spring.ai.mcp.server.instructions=...`

Дополнительная настройка для инструмента документации:

- `dui.mcp.documentation-path` — путь к каталогу с документацией для `DocumentationMcpTool`.
    - по умолчанию: `docs/instruction`

### Практические заметки

- Для `listCollections` необходимо передавать `userId`, иначе инструмент вернет ошибку валидации.
- `getDocumentationFile` защищен от path traversal (файл читается только внутри `dui.mcp.documentation-path`).
- Если каталог документации не существует, `listDocumentationFiles` вернет ошибку конфигурации.

## Как запустить локально
Из корня репозитория:

```bash
./gradlew :dui-service:bootRun
```

Или собрать jar:

```bash
./gradlew :dui-service:clean :dui-service:build
java -jar service/build/libs/dui-service-*.jar
```

## Обязательные переменные окружения
Без этих переменных сервис не поднимется в штатном режиме:

- `POSTGRES_SERVER` — хост PostgreSQL.
- `POSTGRES_PORT` — порт PostgreSQL.
- `POSTGRES_USER` — пользователь БД.
- `POSTGRES_PASSWORD` — пароль БД.
- `KAFKA_SERVERS` — bootstrap servers Kafka.
- `PRINT_SERVICE_URL` — URL внешнего print-service.
- `AUTH_SERVICE_URL` — URL auth-service.
- `AUTH_SYSTEM_SERVICE_ID` — service id для системной авторизации.
- `AUTH_SYSTEM_SERVICE_SECRET` — service secret для системной авторизации.
- `KAFKA_SSL_KEY_STORE_LOCATION` — путь к keystore (если `KAFKA_SSL_ENABLED=true`).
- `KAFKA_SSL_KEY_STORE_PASSWORD` — пароль keystore.
- `KAFKA_SSL_KEY_PASSWORD` — пароль ключа.
- `KAFKA_SSL_TRUST_STORE_LOCATION` — путь к truststore.
- `KAFKA_SSL_TRUST_STORE_PASSWORD` — пароль truststore.

## Необязательные переменные окружения (с дефолтами)

- `SERVER_PORT` (по умолчанию `8080`).
- `POSTGRES_DB` (по умолчанию `duiservice`).
- `POSTGRES_SCHEMA` (по умолчанию `dui_service`).
- `POSTGRES_POOL_SIZE` (по умолчанию `10`).
- `POSTGRES_CONNECTION_TIMEOUT` (по умолчанию `2000`).
- `KAFKA_SSL_ENABLED` (по умолчанию `true`).
- `GROUP_CONSUMER_CONCURRENCY` (по умолчанию `5`).
- `AUTHORIZATION_ENABLED` (по умолчанию `true`).
- `LOGGING_ENABLED` (по умолчанию `true`).
- `LOG_TYPE` (по умолчанию `logstash`).
- `LOGGING_MASKING_ENABLED` (по умолчанию `false`).
- `LOG_HOST` (по умолчанию `localhost`).
- `LOG_PORT` (по умолчанию `24224`).