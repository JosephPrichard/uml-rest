package com.turbouml.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResponseUtils
{
    public static String getResponse(String... messages)
    {
        try {
            return Serializer.serialize(messages);
        } catch(IOException ex) {
            //fatal error
            ex.printStackTrace();
            return "Error";
        }
    }

    public static String getErrorResponse(String... messages)
    {
        try {
            List<String> errorMessages = new ArrayList<>();
            for(String message : messages) {
                errorMessages.add(
                    message.substring(
                        message.indexOf(":")+1
                    )
                );
            }
            try {
                return Serializer.serialize(errorMessages);
            } catch(IOException ex) {
                //fatal error
                ex.printStackTrace();
                return "Error";
            }
        } catch(Exception e) {
            return getResponse(messages);
        }
    }
}
