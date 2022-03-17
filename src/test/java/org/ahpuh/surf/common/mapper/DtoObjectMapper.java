package org.ahpuh.surf.common.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DtoObjectMapper {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> String mapToString(final T dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (final JsonProcessingException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static <T> byte[] mapToByte(final T dto) {
        try {
            return objectMapper.writeValueAsBytes(dto);
        } catch (final JsonProcessingException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
