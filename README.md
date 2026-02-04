# Dynamic Sandbox

Dynamic Sandbox — серверная часть приложения, предоставляющая инструменты для онлайн-конструирования страниц на основе Dynamic UI. Проект реализован на Java и Kotlin с использованием Spring Boot, Spring Security, JPA и Keycloak для аутентификации.

---

## Содержание

- [Описание проекта](#описание-проекта)
- [Технологии](#технологии)
- [Структура проекта](#структура-проекта)
- [Правила запуска](#правила-запуска)
- [Конфигурация](#конфигурация)
- [Тестирование](#тестирование)
- [API](#api)
- [Безопасность](#безопасность)
- [Миграции базы данных](#миграции-базы-данных)

---

## Описание проекта

Dynamic Sandbox предоставляет REST API для управления коллекциями страниц и шаблонов, а также для экспорта и истории изменений коллекций. Используется Keycloak для аутентификации и авторизации пользователей.

---

## Технологии

- Java 17
- Kotlin 1.9
- Spring Boot 3.1.1
- Spring Security (OAuth2 Resource Server)
- Keycloak (версия 22)
- Spring Data JPA (Hibernate)
- PostgreSQL
- Flyway (миграции БД)
- MockK и SpringMockK (тестирование)
- SpringDoc OpenAPI (документация API)
- Docker / Kubernetes (Helm charts для деплоя)

---

## Структура проекта

- `src/main/java/ru/dynamic/sandbox/application/collection` — управление коллекциями страниц
- `src/main/java/ru/dynamic/sandbox/application/template` — управление шаблонами страниц и компонентов
- `src/main/java/ru/dynamic/sandbox/application/security` — конфигурация безопасности и интеграция с Keycloak
- `src/main/resources/db/migration` — миграции Flyway для базы данных
- `src/test/java/ru/dynamic/sandbox` — тесты приложения
- `chart/` — Helm chart для деплоя в Kubernetes

---

## Правила запуска

### Требования

- JDK 17+
- PostgreSQL (рекомендуется версия 12+)
- Keycloak (для аутентификации)
- Maven или Gradle (для сборки)
- Docker и Kubernetes (опционально, для деплоя)

### Локальный запуск

1. **Настройка базы данных**

    - Создайте базу данных PostgreSQL.
    - Укажите параметры подключения в `application.yml` или `application-test.yml`.

2. **Настройка Keycloak**

    - Разверните Keycloak (например, локально или в контейнере).
    - Создайте realm и клиента согласно настройкам в `application.yml`.
    - Импортируйте необходимые роли и пользователей.

3. **Сборка и запуск**

   Используйте Gradle:

   ```bash
   ./gradlew clean build
   ./gradlew bootRun
   ```

   Или Maven:

   ```bash
   mvn clean package
   mvn spring-boot:run
   ```

4. **Доступ к API**

   По умолчанию приложение запускается на `http://localhost:8080`.

5. **Swagger UI**

   Документация API доступна по адресу:

   ```
   http://localhost:8080/v3/api-docs
   http://localhost:8080/swagger-ui.html
   ```

---

## Конфигурация

- **Безопасность**

  Конфигурация безопасности находится в `SecurityConfiguration.java`. Используется OAuth2 Resource Server с JWT, интеграция с Keycloak.

- **CORS**

  Фильтр CORS реализован в `CorsFilter.java`, разрешает все источники и методы.

- **Flyway**

  Миграции базы данных находятся в `src/main/resources/db/migration`.

- **Helm**

  Для деплоя в Kubernetes используется Helm chart в папке `chart/`.

---

## Тестирование

- Используется JUnit 5, MockK и SpringMockK.
- Тесты контроллеров покрывают основные REST эндпоинты.
- Для интеграционных тестов используется `AbstractIntegrationTest` с `MockMvc`.
- Тесты запускаются командой:

  ```bash
  ./gradlew test
  ```

---

## API

Основные эндпоинты:

- **Коллекции**

    - `POST /v1/collection/list` — получить список коллекций пользователя
    - `GET /v1/collection/{collectionUUID}` — получить коллекцию по UUID
    - `GET /v1/collection/{collectionUUID}/export` — экспорт коллекции в JSON
    - `POST /v1/collection/{collectionUUID}/history` — получить историю изменений коллекции
    - `GET /v1/collection/{collectionUUID}/history/{historyUUID}` — получить коллекцию по истории
    - `POST /v1/collection` — создать коллекцию
    - `PUT /v1/collection/{collectionUUID}` — редактировать коллекцию
    - `DELETE /v1/collection/{collectionUUID}` — удалить коллекцию

- **Шаблоны**

    - `POST /v1/template/list` — получить список шаблонов с фильтрацией
    - `GET /v1/template/{uuid}/page` — получить JSON страницы шаблона
    - `GET /v1/template/{uuid}/image` — получить изображение шаблона

- **Аутентификация**

    - `GET /v1/auth/logout` — выход из системы

---

## Безопасность

- Используется OAuth2 Resource Server с JWT.
- Интеграция с Keycloak для управления пользователями и ролями.
- В `SecurityConfiguration` разрешены без авторизации только Swagger, H2 console и некоторые OPTIONS запросы.
- Все остальные запросы требуют аутентификации.

---

## Миграции базы данных

- Используется Flyway.
- Скрипты миграций находятся в `src/main/resources/db/migration`.
- Основные таблицы: `collection`, `template`.
- В миграциях добавляются шаблоны страниц и компонентов.

---

# Дополнительно

- Для локальной разработки можно использовать встроенную H2 базу, но в продакшене — PostgreSQL.
- Для деплоя в Kubernetes используйте Helm chart из папки `chart/`.
- Для работы с Keycloak настройте realm и клиента согласно документации Keycloak и настройкам в проекте.

---
