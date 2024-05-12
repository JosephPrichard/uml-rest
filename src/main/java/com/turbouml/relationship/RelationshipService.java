package com.turbouml.relationship;

import com.turbouml.auth.AuthService;
import com.turbouml.exceptions.AccessDeniedException;
import com.turbouml.exceptions.ResourceScopeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service("RelationshipService")
public class RelationshipService {
    private final RelationshipDao relationshipDao;
    private final AuthService authService;

    @Autowired
    public RelationshipService(
        @Qualifier("RelationshipDao") RelationshipDao relationshipDao,
        @Qualifier("AuthorizationService") AuthService authService
    ) {
        this.relationshipDao = relationshipDao;
        this.authService = authService;
    }

    public void saveRelationship(String userId, RelationshipDto newRelationship)
        throws AccessDeniedException, ResourceScopeException, ResponseStatusException {
        if (newRelationship.getClassIdTo().equals(newRelationship.getClassIdFrom())
            && (newRelationship.getType() == RelationshipType.INHERITANCE
            || newRelationship.getType() == RelationshipType.REALIZATION)
        ) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Class cannot have an inheritance or realization relationship with itself"
            );
        }
        authService.areBothClassesFromSameProject(newRelationship.getClassIdTo(), newRelationship.getClassIdFrom());
        authService.authorizeClassAccess(userId, newRelationship.getClassIdTo());
        relationshipDao.save(newRelationship);
    }

    public List<RelationshipDto> retrieveAllRelationships(String userId, String projectId)
        throws AccessDeniedException {
        authService.authorizeProjectAccess(userId, projectId);
        return relationshipDao.findByProjectId(projectId);
    }

    public void renameRelationship(String userId, String relationshipId, String label)
        throws AccessDeniedException {
        authService.authorizeRelationshipAccess(userId, relationshipId);
        relationshipDao.rename(relationshipId, label);
    }

    public void deleteRelationship(String userId, String relationshipId)
        throws AccessDeniedException {
        authService.authorizeRelationshipAccess(userId, relationshipId);
        relationshipDao.delete(relationshipId);
    }
}
