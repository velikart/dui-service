package ru.axenix.smartax.dui.service.application.page.model;

import java.util.UUID;

/**
 * Упрощенная модель получения списка страниц разделов
 */
public record PageShortDto(UUID id, String name, String title) {
}