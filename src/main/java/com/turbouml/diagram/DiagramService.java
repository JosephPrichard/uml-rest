package com.turbouml.diagram;

import com.turbouml.auth.AuthService;
import com.turbouml.project.ProjectDao;
import com.turbouml.relationship.RelationshipDao;
import com.turbouml.packageuml.PackageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.zip.ZipOutputStream;

@Service("UMLDiagramService")
public class DiagramService {
    private final ProjectDao projectDao;
    private final DiagramDao classDao;
    private final PackageDao packageDao;
    private final RelationshipDao relationshipDao;
    private final AuthService authService;

    @Autowired
    public DiagramService(
        ProjectDao projectDao,
        DiagramDao classDao,
        PackageDao packageDao,
        RelationshipDao relationshipDao,
        AuthService authService
    ) {
        this.projectDao = projectDao;
        this.classDao = classDao;
        this.packageDao = packageDao;
        this.relationshipDao = relationshipDao;
        this.authService = authService;
    }

    public ProjectDiagramDto retrieveDiagram(String userId, String projectId) throws SQLException {
        authService.authorizeProjectAccess(userId, projectId);

        var project = projectDao.findProjectById(projectId);
        var classRelationships = relationshipDao.findByProjectId(projectId);
        var packages = packageDao.findByProjectId(projectId);
        var classes = classDao.findByProjectId(projectId);

        var diagram = new ProjectDiagramDto();
        diagram.setProject(project);
        diagram.setClassRelationships(classRelationships);
        diagram.setPackages(packages);
        diagram.setClassDiagrams(classes);

        return diagram;
    }

}
