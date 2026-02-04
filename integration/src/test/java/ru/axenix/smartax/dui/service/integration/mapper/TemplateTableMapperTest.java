package ru.axenix.smartax.dui.service.integration.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.axenix.smartax.dui.service.model.table.TableResponse;
import ru.axenix.smartax.printservice.model.templateview.find.FindTemplateResponseDto;
import ru.axenix.smartax.printservice.model.templateview.find.TemplateMeta;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тесты для {@link TemplateTableMapper}.
 */
class TemplateTableMapperTest {

    private static final String KEY_TEMPLATE_ID = "templateId";
    private static final String KEY_TEMPLATE_CODE = "templateCode";
    private static final String KEY_TEMPLATE_GROUP = "templateGroup";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_SCHEMA = "schema";
    private static final String KEY_CREATION_DATE = "creationDate";
    private static final String KEY_UPDATE_DATE = "updateDate";

    private static final String VALUE_CODE_1 = "CODE_1";
    private static final String VALUE_CODE_2 = "CODE_2";
    private static final String VALUE_CODE_3 = "CODE_3";

    private static final String VALUE_GROUP_1 = "GROUP_1";
    private static final String VALUE_GROUP_2 = "GROUP_2";
    private static final String VALUE_GROUP_3 = "GROUP_3";

    private static final String VALUE_DESCRIPTION_1 = "Description";
    private static final String VALUE_DESCRIPTION_2 = "Test description";
    private static final String VALUE_DESCRIPTION_3 = "Some description";

    private static final String SCHEMA_KEY_1 = "key";
    private static final String SCHEMA_VALUE_1 = "value";

    private static final String SCHEMA_KEY_2 = "field";
    private static final String SCHEMA_VALUE_2 = "value";

    private static final String SCHEMA_KEY_3 = "s";
    private static final String SCHEMA_VALUE_3 = "v";

    private final TemplateTableMapper mapper = Mappers.getMapper(TemplateTableMapper.class);

    @Test
    void toTableResponseShouldReturnTotalZeroWhenPageIsNull() {
        TemplateMeta meta = new TemplateMeta();
        UUID templateId = UUID.randomUUID();
        meta.setTemplateId(templateId);
        meta.setTemplateCode(VALUE_CODE_1);
        meta.setTemplateGroup(VALUE_GROUP_1);
        meta.setDescription(VALUE_DESCRIPTION_1);
        meta.setSchema(Collections.singletonMap(SCHEMA_KEY_1, SCHEMA_VALUE_1));
        LocalDateTime now = LocalDateTime.now();
        meta.setCreationDate(now);
        meta.setUpdateDate(now);

        FindTemplateResponseDto source = new FindTemplateResponseDto();
        source.setTemplates(Collections.singletonList(meta));

        TableResponse result = mapper.toTableResponse(source);

        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertNotNull(result.getRecords());
        assertEquals(1, result.getRecords().size());

        Map<String, Object> record = result.getRecords().get(0);
        assertEquals(templateId, record.get(KEY_TEMPLATE_ID));
        assertEquals(VALUE_CODE_1, record.get(KEY_TEMPLATE_CODE));
        assertEquals(VALUE_GROUP_1, record.get(KEY_TEMPLATE_GROUP));
        assertEquals(VALUE_DESCRIPTION_1, record.get(KEY_DESCRIPTION));
        assertEquals(meta.getSchema(), record.get(KEY_SCHEMA));
        assertEquals(now, record.get(KEY_CREATION_DATE));
        assertEquals(now, record.get(KEY_UPDATE_DATE));
    }

    @Test
    void templatesToRecordsShouldReturnEmptyListWhenTemplatesNull() {
        List<Map<String, Object>> records = mapper.templatesToRecords(null);

        assertNotNull(records);
        assertTrue(records.isEmpty());
    }

    @Test
    void templatesToRecordsShouldMapListOfTemplates() {
        TemplateMeta meta = new TemplateMeta();
        UUID templateId = UUID.randomUUID();
        meta.setTemplateId(templateId);
        meta.setTemplateCode(VALUE_CODE_2);
        meta.setTemplateGroup(VALUE_GROUP_2);
        meta.setDescription(VALUE_DESCRIPTION_2);
        meta.setSchema(Collections.singletonMap(SCHEMA_KEY_2, SCHEMA_VALUE_2));
        LocalDateTime created = LocalDateTime.now().minusDays(1L);
        LocalDateTime updated = LocalDateTime.now();
        meta.setCreationDate(created);
        meta.setUpdateDate(updated);

        List<Map<String, Object>> records =
                mapper.templatesToRecords(Collections.singletonList(meta));

        assertNotNull(records);
        assertEquals(1, records.size());

        Map<String, Object> record = records.get(0);
        assertEquals(templateId, record.get(KEY_TEMPLATE_ID));
        assertEquals(VALUE_CODE_2, record.get(KEY_TEMPLATE_CODE));
        assertEquals(VALUE_GROUP_2, record.get(KEY_TEMPLATE_GROUP));
        assertEquals(VALUE_DESCRIPTION_2, record.get(KEY_DESCRIPTION));
        assertEquals(meta.getSchema(), record.get(KEY_SCHEMA));
        assertEquals(created, record.get(KEY_CREATION_DATE));
        assertEquals(updated, record.get(KEY_UPDATE_DATE));
    }

    @Test
    void templateToRecordShouldReturnEmptyMapWhenTemplateNull() {
        Map<String, Object> record = mapper.templateToRecord(null);

        assertNotNull(record);
        assertTrue(record.isEmpty());
    }

    @Test
    void templateToRecordShouldMapAllFields() {
        TemplateMeta meta = new TemplateMeta();
        UUID templateId = UUID.randomUUID();
        meta.setTemplateId(templateId);
        meta.setTemplateCode(VALUE_CODE_3);
        meta.setTemplateGroup(VALUE_GROUP_3);
        meta.setDescription(VALUE_DESCRIPTION_3);
        meta.setSchema(Collections.singletonMap(SCHEMA_KEY_3, SCHEMA_VALUE_3));
        LocalDateTime created = LocalDateTime.now().minusHours(2L);
        LocalDateTime updated = LocalDateTime.now().minusHours(1L);
        meta.setCreationDate(created);
        meta.setUpdateDate(updated);

        Map<String, Object> record = mapper.templateToRecord(meta);

        assertNotNull(record);
        assertEquals(templateId, record.get(KEY_TEMPLATE_ID));
        assertEquals(VALUE_CODE_3, record.get(KEY_TEMPLATE_CODE));
        assertEquals(VALUE_GROUP_3, record.get(KEY_TEMPLATE_GROUP));
        assertEquals(VALUE_DESCRIPTION_3, record.get(KEY_DESCRIPTION));
        assertEquals(meta.getSchema(), record.get(KEY_SCHEMA));
        assertEquals(created, record.get(KEY_CREATION_DATE));
        assertEquals(updated, record.get(KEY_UPDATE_DATE));
    }
}
