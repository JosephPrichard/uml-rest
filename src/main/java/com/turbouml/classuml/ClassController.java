package com.turbouml.classuml;

import com.turbouml.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.List;

import static com.turbouml.diagram.ProjectDiagramDto.MAX_X;
import static com.turbouml.diagram.ProjectDiagramDto.MAX_Y;

@Validated
@RestController
public class ClassController {
    private final ClassService classService;

    @Autowired
    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @ResponseBody
    @PostMapping(value = "/classes/create", produces = "application/json")
    public ResponseEntity<String> createClass(
        @Size(min = 4, max = 30) @RequestParam String name,
        @RequestParam String projectId,
        @Valid @RequestParam Access access,
        @Valid @Min(0) @Max(MAX_X) @RequestParam int xPos,
        @Valid @Min(0) @Max(MAX_Y) @RequestParam int yPos
    ) {
        String userId = Session.userIdContext();

        var newClass = new ClassDto();
        newClass.setClassId(ID.generate());
        newClass.setContentName(name);
        newClass.setAccess(access);
        newClass.setStereotypes(new String[]{});
        newClass.setXPos(xPos);
        newClass.setYPos(yPos);
        newClass.setProjectId(projectId);

        try {
            classService.saveClass(userId, newClass);
            return new ResponseEntity<>(
                Serializer.serialize(newClass),
                HttpStatus.OK
            );
        } catch (DataAccessException | IOException ex) {
            Log.exception(ex);
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @ResponseBody
    @GetMapping(value = "/classes/getById", produces = "application/json")
    public ResponseEntity<String> findClass(
        @RequestParam String classId
    ) {
        String userId = Session.userIdContext();
        try {
            var clazz = classService.retrieveClass(userId, classId);
            return new ResponseEntity<>(Serializer.serialize(clazz), HttpStatus.OK);
        } catch (DataAccessException | IOException ex) {
            Log.exception(ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @GetMapping(value = "/classes/forProject", produces = "application/json")
    public ResponseEntity<String> findClassForProject(@RequestParam String projectId) {
        String userId = Session.userIdContext();
        try {
            var clazzList = classService.retrieveAllClasses(userId, projectId);
            return new ResponseEntity<>(Serializer.serialize(clazzList), HttpStatus.OK);
        } catch (DataAccessException | IOException ex) {
            Log.exception(ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @PostMapping(value = "/classes/updateStereotype", produces = "application/json")
    public ResponseEntity<?> updateClassStereotype(
        @RequestParam String classId,
        @RequestParam String[] stereotype
    ) {
        String userId = Session.userIdContext();
        try {
            classService.updateClassStereotype(userId, classId, stereotype);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataAccessException ex) {
            Log.exception(ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @PostMapping(value = "/classes/rename", produces = "application/json")
    public ResponseEntity<?> renameClass(
        @RequestParam String classId,
        @Size(min = 4, max = 30) @RequestParam String name
    ) {
        String userId = Session.userIdContext();
        try {
            classService.renameClass(userId, classId, name);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataAccessException ex) {
            Log.exception(ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @PostMapping(value = "/classes/move", produces = "application/json")
    public ResponseEntity<String> moveClass(
        @RequestParam(value = "class_id") String classId,
        @Valid @Min(0) @Max(MAX_X) @RequestParam int xPos,
        @Valid @Min(0) @Max(MAX_Y) @RequestParam int yPos
    ) {
        String userId = Session.userIdContext();
        try {
            classService.moveClass(userId, classId, xPos, yPos);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataAccessException ex) {
            Log.exception(ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @PostMapping(value = "/classes/delete", produces = "application/json")
    public ResponseEntity<String> deleteClass(
        @RequestParam List<String> classId
    ) {
        String userId = Session.userIdContext();
        try {
            if (classId.size() == 1) {
                classService.deleteClass(userId, classId.get(0));
            } else {
                classService.deleteClasses(userId, classId);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataAccessException ex) {
            Log.exception(ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
        return new ResponseEntity<>(ResponseUtils.getResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}