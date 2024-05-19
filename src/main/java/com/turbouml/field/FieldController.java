package com.turbouml.field;

import com.turbouml.utils.*;
import com.turbouml.classuml.Access;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;


@Validated
@RestController
public class FieldController {
    private final FieldService fieldService;

    @Autowired
    public FieldController(FieldService fieldService) {
        this.fieldService = fieldService;
    }

    @ResponseBody
    @PostMapping(value = "/fields/create")
    public ResponseEntity<String> createField(
        @RequestParam String name,
        @RequestParam(required = false) String dataType,
        @RequestParam String classId,
        @Valid @RequestParam Access access,
        @RequestParam boolean isStatic
    ) {
        String userId = Session.userIdContext();

        var newField = new FieldDto();
        newField.setFieldId(ID.generate());
        newField.setContentName(name);
        newField.setDataType(dataType);
        newField.setClassId(classId);
        newField.setAccess(access);
        newField.setStatic(isStatic);

        try {
            fieldService.saveField(userId, newField);
            return new ResponseEntity<>(Serializer.serialize(newField), HttpStatus.OK);
        } catch (DataAccessException | IOException ex) {
            Log.exception(ex);
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @ResponseBody
    @GetMapping(value = "/fields/forClass")
    public ResponseEntity<String> findFieldForClass(@RequestParam String classId) {
        String userId = Session.userIdContext();
        try {
            var fields = fieldService.retrieveAllFields(userId, classId);
            return new ResponseEntity<>(Serializer.serialize(fields), HttpStatus.OK);
        } catch (DataAccessException | IOException ex) {
            Log.exception(ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @PostMapping(value = "/fields/update")
    public ResponseEntity<String> updateField(
        @RequestParam String name,
        @RequestParam(required = false) String dataType,
        @Valid @RequestParam Access access,
        @RequestParam boolean isStatic,
        @RequestParam List<String> fieldId
    ) {
        String userId = Session.userIdContext();
        try {
            var updatedField = new FieldDto();
            updatedField.setContentName(name);
            updatedField.setAccess(access);
            updatedField.setStatic(isStatic);
            updatedField.setDataType(dataType);

            if (fieldId.size() == 1) {
                updatedField.setFieldId(fieldId.get(0));
                fieldService.updateField(userId, updatedField);
            } else {
                fieldService.batchUpdateFields(userId, updatedField, fieldId);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataAccessException ex) {
            Log.exception(ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @PostMapping(value = "/fields/move")
    public ResponseEntity<String> moveField(
        @RequestParam(required = false) String fieldIdNewOrder,
        @RequestParam String fieldIdToMove
    ) {
        String userId = Session.userIdContext();
        try {
            fieldService.moveField(userId, fieldIdToMove, fieldIdNewOrder);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataAccessException ex) {
            Log.exception(ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @PostMapping(value = "/fields/delete")
    public ResponseEntity<String> deleteField(@RequestParam String fieldId) {
        String userId = Session.userIdContext();
        try {
            fieldService.deleteField(userId, fieldId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataAccessException ex) {
            Log.exception(ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
        return new ResponseEntity<>(
            ResponseUtils.getResponse(ex.getMessage()),
            HttpStatus.BAD_REQUEST
        );
    }
}
