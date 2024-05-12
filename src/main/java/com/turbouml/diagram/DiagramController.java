package com.turbouml.diagram;

import com.turbouml.utils.Serializer;
import com.turbouml.utils.Session;
import com.turbouml.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.sql.SQLException;

@RestController
public class DiagramController {
    private final DiagramService umlDiagramService;

    @Autowired
    public DiagramController(DiagramService umlDiagramService) {
        this.umlDiagramService = umlDiagramService;
    }

    @ResponseBody
    @GetMapping(value = "/diagrams/get", produces = "application/json")
    public ResponseEntity<String> findDiagramForProject(@RequestParam String projectId) {
        String userId = Session.userIdContext();
        try {
            var diagram = umlDiagramService.retrieveDiagram(userId, projectId);
            return new ResponseEntity<>(
                Serializer.serialize(diagram),
                HttpStatus.OK
            );
        } catch (DataAccessException | IOException | SQLException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
        return new ResponseEntity<>(ResponseUtils.getResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
