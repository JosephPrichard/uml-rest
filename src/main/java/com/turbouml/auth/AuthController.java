package com.turbouml.auth;

import com.turbouml.utils.Serializer;
import com.turbouml.utils.Session;
import com.turbouml.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @ResponseBody
    @PostMapping(value = "/auth/login", produces = "application/json")
    public ResponseEntity<String> login(@RequestParam String idToken) {
        try {
            Session.setUserForContext(authService.authenticateUser(idToken));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (GeneralSecurityException | IOException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseBody
    @PostMapping(value = "/auth/logout", produces = "application/json")
    public ResponseEntity<String> logout() {
        Session.removeUserForContext();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/auth/account", produces = "application/json")
    public ResponseEntity<String> account() {
        try {
            return new ResponseEntity<>(
                Serializer.serialize(Session.userContext()),
                HttpStatus.OK
            );
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
        return new ResponseEntity<>(ResponseUtils.getResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
