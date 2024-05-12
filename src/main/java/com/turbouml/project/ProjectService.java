package com.turbouml.project;

import com.turbouml.auth.AuthService;
import com.turbouml.exceptions.AccessDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ProjectService")
public class ProjectService {
    private final ProjectDao projectDao;
    private final AuthService authService;

    @Autowired
    public ProjectService(
        @Qualifier("ProjectDao") ProjectDao projectDao,
        @Qualifier("AuthorizationService") AuthService authService
    ) {
        this.projectDao = projectDao;
        this.authService = authService;
    }

    public void saveProject(String userId, ProjectDto newProject)
        throws AccessDeniedException {
        if (userId != null) {
            projectDao.save(newProject);
        } else {
            throw new AccessDeniedException("You need to be logged in to make a project");
        }
    }

    public ProjectDto retrieveProject(String userId, String projectId)
        throws AccessDeniedException {
        authService.authorizeProjectAccess(userId, projectId);
        return projectDao.findProjectById(projectId);
    }

    public List<ProjectDto> retrieveAllProjects(String userId) {
        return projectDao.findProjectsForUser(userId);
    }

    public void renameProject(String userId, String projectId, String newName)
        throws AccessDeniedException {
        authService.authorizeProjectAccess(userId, projectId);
        projectDao.rename(projectId, newName);
    }

    public void deleteProject(String userId, String projectId)
        throws AccessDeniedException {
        authService.authorizeProjectAccess(userId, projectId);
        projectDao.delete(projectId);
    }

}
