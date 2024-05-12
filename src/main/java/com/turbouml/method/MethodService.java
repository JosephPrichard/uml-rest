package com.turbouml.method;

import com.turbouml.auth.AuthService;
import com.turbouml.exceptions.AccessDeniedException;
import com.turbouml.exceptions.InvalidArgumentsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.List;

@Service("MethodService")
public class MethodService {
    private final MethodDao methodDao;
    private final AuthService authService;

    @Autowired
    public MethodService(
        @Qualifier("MethodDao") MethodDao methodDao,
        @Qualifier("AuthorizationService") AuthService authService
    ) {
        this.methodDao = methodDao;
        this.authService = authService;
    }

    public void saveMethod(String userId, MethodEntity newMethod)
        throws AccessDeniedException, InvalidArgumentsException {
        if (newMethod.isAbstract() && newMethod.isStatic()) {
            throw new InvalidArgumentsException("Method can't be both abstract and static");
        }
        authService.authorizeClassAccess(userId, newMethod.getClassId());
        methodDao.save(newMethod);
    }

    public List<MethodEntity> retrieveAllMethods(String userId, String classId)
        throws AccessDeniedException {
        authService.authorizeClassAccess(userId, classId);
        return methodDao.findByClassId(classId);
    }

    public void updateMethod(String userId, MethodEntity updatedMethod)
        throws AccessDeniedException {
        if (updatedMethod.isAbstract() && updatedMethod.isStatic()) {
            throw new InvalidArgumentsException("Method can't be both abstract and static");
        }
        authService.authorizeMethodAccess(userId, updatedMethod.getMethodId());
        methodDao.update(updatedMethod);
    }

    public void batchUpdateMethods(String userId, MethodEntity updatedMethod, List<String> methodIdsToUpdate)
        throws AccessDeniedException {
        if (updatedMethod.isAbstract() && updatedMethod.isStatic()) {
            throw new InvalidArgumentsException("Method can't be both abstract and static");
        }
        authService.authorizeBatchMethodAccess(userId, methodIdsToUpdate);
        methodDao.batchUpdate(updatedMethod, methodIdsToUpdate);
    }

    public void moveMethod(String userId, String methodIdToMove, @Nullable String methodIdNewPos)
        throws AccessDeniedException {
        String classId = authService.areBothMethodsFromSameClassReturnClassId(methodIdToMove, methodIdNewPos);
        authService.authorizeMethodAccess(userId, methodIdToMove);
        if (methodIdNewPos != null) {
            methodDao.move(methodIdToMove, methodIdNewPos, classId);
        } else {
            methodDao.moveToEnd(methodIdToMove, classId);
        }
        methodDao.move(methodIdToMove, methodIdNewPos, classId);
    }

    public void deleteMethod(String userId, String methodId)
        throws AccessDeniedException {
        authService.authorizeMethodAccess(userId, methodId);
        methodDao.delete(methodId);
    }

}
