package com.turbouml.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.turbouml.exceptions.AccessDeniedException;
import com.turbouml.exceptions.InvalidTokenException;
import com.turbouml.exceptions.ResourceScopeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service("AuthorizationService")
public class AuthService {
    private static final String CLIENT_ID = "693479373927-m0ql8ei4gfq2ja8goc5e53h0ue64r7s1.apps.googleusercontent.com";

    private final AuthDao authDao;

    private final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
        .Builder(new NetHttpTransport(), new GsonFactory())
        .setAudience(Collections.singletonList(CLIENT_ID))
        .build();

    @Autowired
    public AuthService(@Qualifier("AuthorizationDao") AuthDao authDao) {
        this.authDao = authDao;
    }

    public User authenticateUser(String idTokenString)
        throws GeneralSecurityException, IOException {
        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            var userId = payload.getSubject();
            var email = payload.getEmail();
            var name = (String) payload.get("name");

            var user = new User();
            user.setId(userId);
            user.setEmail(email);
            user.setName(name);

            return user;
        } else {
            throw new InvalidTokenException("Invalid ID Token");
        }
    }

    public void authorizeProjectAccess(@Nullable String userId, String projectId)
        throws AccessDeniedException {
        if (userId == null || !authDao.canAccessProject(userId, projectId)) {
            throw new AccessDeniedException("You're not authorized to access this project");
        }
    }

    public void authorizeClassAccess(@Nullable String userId, String classId)
        throws AccessDeniedException {
        if (userId == null || !authDao.canAccessClass(userId, classId)) {
            throw new AccessDeniedException("You're not authorized to access this resource");
        }
    }

    public void authorizeClassesAccess(@Nullable String userId, List<String> classIds)
        throws AccessDeniedException {
        if (userId == null || !authDao.canAccessClasses(userId, classIds)) {
            throw new AccessDeniedException("You're not authorized to access these resources");
        }
    }

    public void authorizePackageAccess(@Nullable String userId, String packageId)
        throws AccessDeniedException {
        if (userId == null || !authDao.canAccessPackage(userId, packageId)) {
            throw new AccessDeniedException("You're not authorized to access this resource");
        }
    }

    public void authorizeFieldAccess(@Nullable String userId, String fieldId)
        throws AccessDeniedException {
        if (userId == null || !authDao.canAccessField(userId, fieldId)) {
            throw new AccessDeniedException("You're not authorized to access this resource");
        }
    }

    public void authorizeBatchFieldAccess(@Nullable String userId, List<String> fieldIds)
        throws AccessDeniedException {
        if (userId == null || !authDao.canAccessFields(userId, fieldIds)) {
            throw new AccessDeniedException("You're not authorized to access these resources");
        }
    }

    public String areBothFieldsFromSameClassReturnClassId(String fieldId1, String fieldId2) {
        String classId = authDao.areBothFieldsFromSameClass(fieldId1, fieldId2);
        if (classId == null) {
            throw new ResourceScopeException("Fields are from two different classes");
        }
        return classId;
    }

    public void authorizeMethodAccess(@Nullable String userId, String methodId)
        throws AccessDeniedException {
        if (userId == null || !authDao.canAccessMethod(userId, methodId)) {
            throw new AccessDeniedException("You're not authorized to access this resource");
        }
    }

    public void authorizeBatchMethodAccess(@Nullable String userId, List<String> methodIds)
        throws AccessDeniedException {
        if (userId == null || !authDao.canAccessMethods(userId, methodIds)) {
            throw new AccessDeniedException("You're not authorized to access these resources");
        }
    }

    public String areBothMethodsFromSameClassReturnClassId(String methodId1, String methodId2) {
        String classId = authDao.areBothMethodsFromSameClass(methodId1, methodId2);
        if (classId == null) {
            throw new ResourceScopeException("Methods are from two different classes");
        }
        return classId;
    }

    public void authorizeRelationshipAccess(@Nullable String userId, String relationshipId)
        throws AccessDeniedException {
        if (userId == null || !authDao.canAccessRelationship(userId, relationshipId)) {
            throw new AccessDeniedException("You're not authorized to access this resource");
        }
    }

    public void areBothClassesFromSameProject(String classId1, String classId2)
        throws ResourceScopeException {
        if (!authDao.areBothClassesFromSameProject(classId1, classId2)) {
            throw new ResourceScopeException("Classes are from two different projects");
        }
    }
}
