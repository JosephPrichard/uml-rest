package com.turbouml.field;

import com.turbouml.auth.AuthService;
import com.turbouml.exceptions.AccessDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.List;

@Service("FieldService")
public class FieldService {
    private final FieldDao fieldDao;
    private final AuthService authService;

    @Autowired
    public FieldService(
        @Qualifier("FieldDao") FieldDao fieldDao,
        @Qualifier("AuthorizationService") AuthService authService
    ) {
        this.fieldDao = fieldDao;
        this.authService = authService;
    }

    public void saveField(String userId, FieldDto newField)
        throws AccessDeniedException {
        authService.authorizeClassAccess(userId, newField.getClassId());
        fieldDao.save(newField);
    }

    public List<FieldDto> retrieveAllFields(String userId, String classId)
        throws AccessDeniedException {
        authService.authorizeClassAccess(userId, classId);
        return fieldDao.findByClassId(classId);
    }

    public void updateField(String userId, FieldDto updatedField)
        throws AccessDeniedException {
        authService.authorizeFieldAccess(userId, updatedField.getFieldId());
        fieldDao.update(updatedField);
    }

    public void batchUpdateFields(String userId, FieldDto updatedField, List<String> fieldIdsToUpdate)
        throws AccessDeniedException {
        authService.authorizeBatchFieldAccess(userId, fieldIdsToUpdate);
        fieldDao.batchUpdate(updatedField, fieldIdsToUpdate);
    }

    public void moveField(String userId, String fieldIdToMove, @Nullable String fieldIdNewPos)
        throws AccessDeniedException {
        String classId = authService.areBothFieldsFromSameClassReturnClassId(fieldIdToMove, fieldIdNewPos);
        authService.authorizeFieldAccess(userId, fieldIdToMove);
        if (fieldIdNewPos != null) {
            fieldDao.move(fieldIdToMove, fieldIdNewPos, classId);
        } else {
            fieldDao.moveToEnd(fieldIdToMove, classId);
        }
        fieldDao.move(fieldIdToMove, fieldIdNewPos, classId);
    }

    public void deleteField(String userId, String fieldId)
        throws AccessDeniedException {
        authService.authorizeFieldAccess(userId, fieldId);
        fieldDao.delete(fieldId);
    }
}
