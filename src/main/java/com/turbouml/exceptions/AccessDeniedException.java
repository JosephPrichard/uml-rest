package com.turbouml.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String msg) {
        super(msg);
    }
}
