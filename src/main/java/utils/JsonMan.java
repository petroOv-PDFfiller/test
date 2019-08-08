package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.microsoft.graph.core.ClientException;

import java.io.IOException;

public class JsonMan {

    private static final ThreadLocal<ObjectMapper> OBJECT_MAPPER_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        return objectMapper;
    });

    public static ObjectMapper getMapperInstance() {
        return OBJECT_MAPPER_THREAD_LOCAL.get();
    }

    /**
     * @param object pojo
     * @return json pretty formatted string
     */
    public static String convertPojoToJsonPrettyString(Object object) {
        try {
            return getMapperInstance()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param jsonString json string to convert
     * @param clazz      target class to parse in
     * @return new instance of clazz
     */
    public static <T> T convertJsonToPojo(final String jsonString, Class<T> clazz) {
        try {
            T response = getMapperInstance().readValue(jsonString, clazz);
            return response;
        } catch (IOException e) {
            throw new ClientException("JSON was parsed with an exception: ", e);
        }
    }

    /**
     * @param jsonString json string to convert
     * @param typeRef    typeRef for parse
     * @return new instance of class according to typeRef
     */
    public static <T> T convertJsonToPojo(final String jsonString, JavaType typeRef) {
        try {
            T response = getMapperInstance().readValue(jsonString, typeRef);
            return response;
        } catch (IOException e) {
            throw new ClientException("JSON was parsed with an exception: ", e);
        }
    }
}
