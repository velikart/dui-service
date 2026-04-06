package ru.axenix.smartax.dui.service.application.page.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.axenix.smartax.dui.service.application.page.domain.PageRepository;
import ru.axenix.smartax.dui.service.application.page.mapper.PageMapper;
import ru.axenix.smartax.dui.service.application.page.model.PageShortDto;
import ru.axenix.smartax.dui.service.application.page.service.PageService;
import ru.axenix.smartax.dui.service.contract.model.PageDto;

import java.util.List;
import java.util.UUID;

import static ru.axenix.smartax.dui.service.error.ErrorDescription.PAGE_NOT_FOUND;

/**
 * Сервис для работы со страницами
 */
@Service
@RequiredArgsConstructor
public class PageServiceImpl implements PageService {

    private final PageRepository pageRepository;
    private final PageMapper pageMapper;

    @Override
    @Transactional(readOnly = true)
    public PageDto getPage(UUID pageId) {
        return pageRepository.findById(pageId)
                .map(pageMapper::toDto)
                .orElseThrow(PAGE_NOT_FOUND::exception);
    }

    @Override
    @Transactional(readOnly = true)
    public PageDto getPageByName(String name) {
        return pageRepository.findByNameEqualsIgnoreCase(name)
                .map(pageMapper::toDto)
                .orElseThrow(PAGE_NOT_FOUND::exception);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PageShortDto> listPages() {
        return pageRepository.findAllByOrderByNameAsc().stream()
                .map(page -> new PageShortDto(page.getId(), page.getName(), page.getTitle()))
                .toList();
    }
}