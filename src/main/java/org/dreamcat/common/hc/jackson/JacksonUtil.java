package org.dreamcat.common.hc.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dreamcat.common.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class JacksonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> Map<String, Object> toMap(T t) {
        Map<String, Object> map = objectMapper
                .convertValue(t, new TypeReference<Map<String, Object>>() {
                });
        return map.entrySet().stream()
                .filter(stringObjectEntry ->
                        stringObjectEntry.getValue() != null)
                .collect(
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static <T> Map<String, String> toProp(T t) {
        Map<String, String> stringMap = objectMapper
                .convertValue(t, new TypeReference<Map<String, Object>>() {
                });

        return stringMap.entrySet().stream()
                .filter(stringEntry -> stringEntry.getValue() != null &&
                        !stringEntry.getValue().equals("null"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Nullable
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Nullable
    public static <T> ArrayList<T> fromJsonArray(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json,
                    new TypeReference<ArrayList<String>>() {});
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Nullable
    public static String toJson(Object bean) {
        try {
            return objectMapper.writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
