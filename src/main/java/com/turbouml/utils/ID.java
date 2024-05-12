package com.turbouml.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class ID
{
    private static final Logger log = LoggerFactory.getLogger(ID.class);

    public static String generate()
    {
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();
        log.info("Generated new ID: " + id);
        return id;
    }
}
