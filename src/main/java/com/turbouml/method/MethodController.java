package com.turbouml.method;

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
public class MethodController {
    private final MethodService methodService;

    @Autowired
    public MethodController(MethodService methodService) {
        this.methodService = methodService;
    }

    @ResponseBody
    @PostMapping(value = "/methods/create")
    public ResponseEntity<String> createMethod(
        @RequestParam String name,
        @RequestParam(required = false) String returnType,
        @RequestParam String classId,
        @Valid @RequestParam Access access,
        @RequestParam boolean isStatic,
        @RequestParam boolean isAbstract,
        @RequestParam String params
    ) {
        String userId = Session.userIdContext();

        var newMethod = new MethodEntity();
        newMethod.setMethodId(ID.generate());
        newMethod.setContentName(name);
        newMethod.setReturnType(returnType);
        newMethod.setParams(params);
        newMethod.setAccess(access);
        newMethod.setStatic(isStatic);
        newMethod.setAbstract(isAbstract);
        newMethod.setClassId(classId);

        try {
            methodService.saveMethod(userId, newMethod);
            return new ResponseEntity<>(
                Serializer.serialize(newMethod),
                HttpStatus.OK
            );
        } catch (DataAccessException | IOException ex) {
            Log.exception(ex);
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @ResponseBody
    @GetMapping(value = "/methods/forClass")
    public ResponseEntity<String> findMethodForClass(String classId) {
        String userId = Session.userIdContext();
        try {
            var methods = methodService.retrieveAllMethods(userId, classId);
            return new ResponseEntity<>(Serializer.serialize(methods), HttpStatus.OK);
        } catch (DataAccessException | IOException ex) {
            Log.exception(ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @PostMapping(value = "/methods/update")
    public ResponseEntity<String> updateMethod(
        @RequestParam String name,
        @RequestParam(required = false) String returnType,
        @RequestParam String params,
        @Valid @RequestParam Access access,
        @RequestParam boolean isStatic,
        @RequestParam boolean isAbstract,
        @RequestParam List<String> methodId
    ) {
        String userId = Session.userIdContext();
        try {
            var updatedMethod = new MethodEntity();
            updatedMethod.setContentName(name);
            updatedMethod.setReturnType(returnType);
            updatedMethod.setParams(params);
            updatedMethod.setAccess(access);
            updatedMethod.setAbstract(isAbstract);
            updatedMethod.setStatic(isStatic);

            if (methodId.size() == 1) {
                updatedMethod.setMethodId(methodId.get(0));
                methodService.updateMethod(userId, updatedMethod);
            } else {
                methodService.batchUpdateMethods(userId, updatedMethod, methodId);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataAccessException ex) {
            Log.exception(ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @PostMapping(value = "/methods/move")
    public ResponseEntity<String> moveMethod(
        @RequestParam(required = false) String methodIdNewOrder,
        @RequestParam String methodIdToMove
    ) {
        String userId = Session.userIdContext();
        try {
            methodService.moveMethod(userId, methodIdToMove, methodIdNewOrder);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataAccessException ex) {
            Log.exception(ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @PostMapping(value = "/methods/delete")
    public ResponseEntity<String> deleteMethod(@RequestParam String methodId) {
        String userId = Session.userIdContext();
        try {
            methodService.deleteMethod(userId, methodId);
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
