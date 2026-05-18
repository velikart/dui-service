package ru.axenix.smartax.dui.service.application.page.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.axenix.smartax.dui.service.application.page.domain.PageRepository;
import ru.axenix.smartax.dui.service.application.page.filter.PermissionFilter;
import ru.axenix.smartax.dui.service.application.page.mapper.PageMapper;
import ru.axenix.smartax.dui.service.application.page.service.PageService;
import ru.axenix.smartax.dui.service.contract.model.PageDto;
import ru.axenix.smartax.dui.service.contract.model.PageShortDto;

import java.util.List;
import java.util.Map;
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
    private final PermissionFilter permissionFilter;

    @Override
    @Transactional(readOnly = true)
    public PageDto getPage(UUID pageId) {
        return pageRepository.findById(pageId)
            .map(pageMapper::toDto)
            .map(this::filterByPermissions)
            .orElseThrow(PAGE_NOT_FOUND::exception);
    }

    @Override
    @Transactional(readOnly = true)
    public PageDto getPageByName(String name) {
        return pageRepository.findByNameEqualsIgnoreCase(name)
            .map(pageMapper::toDto)
            .map(this::filterByPermissions)
            .orElseThrow(PAGE_NOT_FOUND::exception);
    }

    /**
     * Фильтрует содержимое страницы по правам текущего пользователя.
     *
     * @param pageDto страница до фильтрации
     * @return страница после фильтрации
     */
    private PageDto filterByPermissions(PageDto pageDto) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> filteredPages = (List<Map<String, Object>>) permissionFilter.filter(pageDto.getPages());
        pageDto.setPages(filteredPages);
        return pageDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PageShortDto> listPages() {
        return pageRepository.findAllByOrderByNameAsc().stream()
                .map(page -> new PageShortDto(page.getId(), page.getName(), page.getTitle()))
                .toList();
    }

    @Override
    @Transactional
    public PageDto createPage(PageDto page) {
        UUID pageId = UUID.randomUUID();
        var entity = pageMapper.toEntity(page, pageId);
        return pageMapper.toDto(pageRepository.save(entity));
    }

    @Override
    @Transactional
    public PageDto editPage(UUID pageId, PageDto page) {
        var entity = pageRepository.findById(pageId)
            .orElseThrow(PAGE_NOT_FOUND::exception);

        pageMapper.merge(entity, page);

        return pageMapper.toDto(pageRepository.save(entity));
    }
}