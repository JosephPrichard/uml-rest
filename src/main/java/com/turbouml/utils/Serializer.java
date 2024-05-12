package com.turbouml.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Serializer {
    public static String serialize(Object obj, boolean pretty) throws IOException {
        var mapper = new ObjectMapper();
        if (pretty) {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } else {
            return mapper.writeValueAsString(obj);
        }
    }

    public static String serialize(Object obj) throws IOException {
        return serialize(obj, false);
    }
}
