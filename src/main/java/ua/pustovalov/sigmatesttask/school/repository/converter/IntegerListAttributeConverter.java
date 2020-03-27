package ua.pustovalov.sigmatesttask.school.repository.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.List;

@Slf4j
@Converter(autoApply = true)
public class IntegerListAttributeConverter implements AttributeConverter<List<Integer>, String> {

    private final ObjectMapper objectMapper;

    public IntegerListAttributeConverter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Override
    public String convertToDatabaseColumn(List<Integer> objects) {
        try {
            return objectMapper.writeValueAsString(objects);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Integer> convertToEntityAttribute(String dbData) {
        try {
            if (StringUtils.isBlank(dbData)) {
                return null;
            }
            return objectMapper.readValue(dbData, List.class);
        } catch (IOException ex) {
            log.error(ex.getMessage());
            return null;
        }
    }
}
