package ru.axenix.smartax.dui.service.application.page.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.axenix.smartax.dui.service.application.page.domain.PageEntity;
import ru.axenix.smartax.dui.service.application.page.domain.PageRepository;
import ru.axenix.smartax.dui.service.application.page.service.PageService;
import ru.axenix.smartax.dui.service.application.page.model.PageDto;

import java.util.UUID;

import static ru.axenix.smartax.dui.service.error.ErrorDescription.PAGE_NOT_FOUND;

/**
 * Сервис для работы со страницами
 *
 * @author Sergey Dresvyanin
 */
@Service
@RequiredArgsConstructor
public class PageServiceImpl implements PageService {
    private final PageRepository pageRepository;

    /**
     * Получение страницы по Id
     *
     * @param pageId идентификатор страницы
     * @return объект страницы
     */
    @Override
    @Transactional(readOnly = true)
    public PageDto getPage(UUID pageId) {
        return pageRepository.findById(pageId).map(this::pageToDto)
                .orElseThrow(PAGE_NOT_FOUND::exception);
    }

    /**
     * Получение страницы по маршруту
     *
     * @param name маршрут или имя страницы
     * @return объект страницы
     */
    @Override
    @Transactional(readOnly = true)
    public PageDto getPageByName(String name) {
        return pageRepository.findByNameEqualsIgnoreCase(name)
                .map(this::pageToDto)
                .orElseThrow(PAGE_NOT_FOUND::exception);
    }

    private PageDto pageToDto(PageEntity pageEntity) {
        return PageDto.builder()
                .id(pageEntity.getId())
                .title(pageEntity.getTitle())
                .instructions(pageEntity.getInstructions())
                .author(pageEntity.getAuthor())
                .updateDateTime(pageEntity.getUpdateDateTime())
                .build();
    }
}