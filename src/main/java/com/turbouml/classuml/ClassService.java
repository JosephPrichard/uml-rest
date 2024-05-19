package com.turbouml.classuml;

import com.turbouml.auth.AuthService;
import com.turbouml.exceptions.AccessDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ClassService")
public class ClassService {
    private final ClassDao classDao;
    private final AuthService authService;

    @Autowired
    public ClassService(
        @Qualifier("ClassDao") ClassDao classDao,
        @Qualifier("AuthorizationService") AuthService authService
    ) {
        this.classDao = classDao;
        this.authService = authService;
    }

    public void saveClass(String userId, ClassDto newClass) throws AccessDeniedException {
        authService.authorizeProjectAccess(userId, newClass.getProjectId());
        classDao.save(newClass);
    }

    public ClassDto retrieveClass(String userId, String classId) throws AccessDeniedException {
        authService.authorizeClassAccess(userId, classId);
        return classDao.findByClassId(classId);
    }

    public List<ClassDto> retrieveAllClasses(String userId, String projectId) throws AccessDeniedException {
        authService.authorizeProjectAccess(userId, projectId);
        return classDao.findByProjectId(projectId);
    }

    public void updateClassStereotype(String userId, String classId, String[] stereotypes) {
        authService.authorizeClassAccess(userId, classId);
        classDao.updateStereotypes(classId, stereotypes);
    }

    public void renameClass(String userId, String classId, String newName) throws AccessDeniedException {
        authService.authorizeClassAccess(userId, classId);
        classDao.rename(classId, newName);
    }

    public void moveClass(String userId, String classId, int x, int y) throws AccessDeniedException {
        authService.authorizeClassAccess(userId, classId);
        classDao.move(classId, x, y);
    }

    public void deleteClass(String userId, String classId) throws AccessDeniedException {
        authService.authorizeClassAccess(userId, classId);
        classDao.delete(classId);
    }

    public void deleteClasses(String userId, List<String> classIds) throws AccessDeniedException {
        authService.authorizeClassesAccess(userId, classIds);
        classDao.delete(classIds);
    }
}
