# dui-service-mcp-starter

Переиспользуемый Spring Boot starter для MCP-инструментов на базе Spring AI.

## Что делает starter

- Находит Spring-бины, помеченные `@SmartaxMcpTool`.
- Находит в них методы с `@Tool`.
- Регистрирует эти методы в `ToolCallbackProvider` для MCP.
- Формирует описание инструмента по правилу:
    - если markdown-файл найден — **берёт описание из файла**;
    - если файла нет / файл пустой / файл не читается — **берёт `@Tool(description = "...")`**.

---

## Что нужно, чтобы starter заработал

1. Подключить зависимость starter.
2. Иметь в проекте Spring Boot приложение.
3. Создать хотя бы один бин с `@SmartaxMcpTool` и методами `@Tool`.
4. (Опционально) Создать markdown-файлы с описанием в папке `smartax.mcp.tools.description-path`.

### Подключение зависимости

```gradle
implementation "ru.axenix.smartax:dui-service-mcp-starter:<version>"
```

### Минимальный пример инструмента

```java
import org.springframework.ai.tool.annotation.Tool;
import ru.axenix.smartax.mcp.starter.annotation.SmartaxMcpTool;

@SmartaxMcpTool
public class MyMcpTool {

    @Tool(name = "ping", description = "Simple ping tool")
    public String ping() {
        return "pong";
    }
}
```

---

## Конфигурация

### Свойства

`smartax.mcp.tools.description-path` — путь к папке с markdown-описаниями инструментов.

### Значение по умолчанию

```yaml
smartax:
  mcp:
    tools:
      description-path: docs/mcp-tools
```

### Как переопределить путь

```yaml
smartax:
  mcp:
    tools:
      description-path: /opt/app/mcp-descriptions
```

Можно использовать как относительный, так и абсолютный путь.

---

## Аннотации starter

### `@SmartaxMcpTool`

Маркерная аннотация уровня класса. Помечает Spring-бин как MCP tool-контейнер.

**Пример:**

```java
@SmartaxMcpTool
public class CollectionMcpTool {
    // @Tool методы
}
```

### `@ToolDescriptionFile`

Аннотация уровня метода для **переопределения имени markdown-файла** для конкретного инструмента.

- Можно указать значение с `.md` и без `.md`.
- Если аннотация не указана, используется стандартное правило именования.

**Пример:**

```java
import org.springframework.ai.tool.annotation.Tool;
import ru.axenix.smartax.mcp.starter.annotation.ToolDescriptionFile;

@Tool(name = "ping", description = "Simple ping tool")
@ToolDescriptionFile("custom-ping-doc") // будет custom-ping-doc.md
public String ping() {
    return "pong";
}
```

---

## Правило выбора markdown-файла

Приоритет выбора файла описания:

1. `@ToolDescriptionFile("...")` на методе;
2. `@Tool(name = "...")`;
3. имя Java-метода.

Финальный формат имени всегда: `<name>.md`.

### Примеры

- `@Tool(name = "listCollections")` → `listCollections.md`;
- `public String getPageInstructions(...)` (без `name`) → `getPageInstructions.md`;
- `@ToolDescriptionFile("my-doc")` → `my-doc.md`.

---

## Поведение fallback (важно)

Если файл:

- не существует,
- пустой,
- недоступен для чтения,

то используется `@Tool(description = "...")`.

Это поведение позволяет запускать сервис даже без markdown-документации.


---

## Расширение источников описания

В starter добавлен интерфейс `McpToolDescriptionProvider`.
Это позволяет добавлять новые источники описания инструмента (например, YAML, Config Server, БД и т.д.).

По умолчанию подключен `McpToolDescriptionMarkdownProvider`, который читает `.md` файлы из `smartax.mcp.tools.description-path`.

Если нужен альтернативный источник:

1. Реализуйте `McpToolDescriptionProvider`;
2. Зарегистрируйте его как Spring Bean;
3. Он будет участвовать в разрешении description вместе с остальными провайдерами.
