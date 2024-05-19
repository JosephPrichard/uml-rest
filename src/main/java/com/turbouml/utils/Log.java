package com.turbouml.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
    public static final Logger LOGGER  = Logger.getLogger("umlapi-logger");

    public static void exception(Exception ex) {
        Log.LOGGER.log(Level.WARNING, "Caught an exception: ", ex);
    }
}
