package com.turbouml.auth;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;

import java.util.List;

import static org.jooq.schema.tables.Classes.CLASSES;
import static org.jooq.schema.tables.Projects.PROJECTS;
import static org.jooq.schema.tables.Packages.PACKAGES;
import static org.jooq.schema.tables.Fields.FIELDS;
import static org.jooq.schema.tables.Methods.METHODS;
import static org.jooq.schema.tables.Relationships.RELATIONSHIPS;

@Repository("AuthorizationDao")
public class AuthDao {
    private final DSLContext create;

    @Autowired
    public AuthDao(DSLContext create) {
        this.create = create;
    }

    public boolean canAccessProject(String userId, String projectId) {
        return create.fetchExists(
            create.selectOne()
                .from(PROJECTS)
                .where(PROJECTS.PROJECT_ID.eq(projectId))
                .and(PROJECTS.USER_ID.eq(userId))
        );
    }

    public boolean canAccessResource(String userId, Select<? extends Record1<String>> selectProjectIdForResource) {
        return create.fetchExists(
            create.selectOne()
                .from(PROJECTS)
                .where(PROJECTS.PROJECT_ID
                    .eq(selectProjectIdForResource)
                )
                .and(PROJECTS.USER_ID.eq(userId))
        );
    }

    public boolean canAccessClass(String userId, String classId) {
        return canAccessResource(
            userId,
            create.select(CLASSES.PROJECT_ID)
                .from(CLASSES)
                .where(CLASSES.CLASS_ID.eq(classId))
        );
    }

    public boolean canAccessClasses(String userId, List<String> classIds) {
        List<String> allUserIds = create
            .select(PROJECTS.USER_ID)
            .from(PROJECTS)
            .where(PROJECTS.PROJECT_ID
                .in(create.select(CLASSES.PROJECT_ID)
                    .from(CLASSES)
                    .where(CLASSES.CLASS_ID.in(classIds))
                )
            )
            .fetchInto(String.class);

        return allUserIds.size() == 1 && allUserIds.get(0).equals(userId);
    }

    public boolean canAccessPackage(String userId, String packageId) {
        return canAccessResource(
            userId,
            create.select(PACKAGES.PROJECT_ID)
                .from(PACKAGES)
                .where(PACKAGES.PACKAGE_ID.eq(packageId))
        );
    }

    public boolean canAccessField(String userId, String fieldId) {
        return canAccessResource(
            userId,
            create.select(FIELDS.PROJECT_ID)
                .from(FIELDS)
                .where(FIELDS.FIELD_ID.eq(fieldId))
        );
    }

    public boolean canAccessMethod(String userId, String methodId) {
        return canAccessResource(
            userId,
            create.select(METHODS.PROJECT_ID)
                .from(METHODS)
                .where(METHODS.METHOD_ID.eq(methodId))
        );
    }

    public boolean canAccessMethods(String userId, List<String> methodIds) {
        List<String> allUserIds = create
            .select(PROJECTS.USER_ID)
            .from(PROJECTS)
            .where(PROJECTS.PROJECT_ID
                .in(create.select(METHODS.PROJECT_ID)
                    .from(METHODS)
                    .where(METHODS.METHOD_ID.in(methodIds))
                )
            )
            .fetchInto(String.class);

        return allUserIds.size() == 1 && allUserIds.get(0).equals(userId);
    }

    public boolean canAccessFields(String userId, List<String> fieldIds) {
        List<String> allUserIds = create
            .select(PROJECTS.USER_ID)
            .from(PROJECTS)
            .where(PROJECTS.PROJECT_ID
                .in(create.select(FIELDS.PROJECT_ID)
                    .from(FIELDS)
                    .where(FIELDS.FIELD_ID.in(fieldIds))
                )
            )
            .fetchInto(String.class);

        return allUserIds.size() == 1 && allUserIds.get(0).equals(userId);
    }

    public boolean canAccessRelationship(String userId, String relationshipId) {
        return canAccessResource(
            userId,
            create.select(RELATIONSHIPS.PROJECT_ID)
                .from(RELATIONSHIPS)
                .where(RELATIONSHIPS.RELATIONSHIP_ID.eq(relationshipId))
        );
    }

    public boolean areBothClassesFromSameProject(String classId1, String classId2) {
        String projectForClass1 = create
            .select(CLASSES.PROJECT_ID)
            .from(CLASSES)
            .where(CLASSES.CLASS_ID.eq(classId1))
            .fetchInto(String.class)
            .get(0);

        String projectForClass2 = create
            .select(CLASSES.PROJECT_ID)
            .from(CLASSES)
            .where(CLASSES.CLASS_ID.eq(classId2))
            .fetchInto(String.class)
            .get(0);

        return projectForClass1.equals(projectForClass2);
    }

    @Nullable
    public String areBothMethodsFromSameClass(String method1, @Nullable String method2) {
        String classForMethod1 = create
            .select(METHODS.CLASS_ID)
            .from(METHODS)
            .where(METHODS.METHOD_ID.eq(method1))
            .fetchInto(String.class)
            .get(0);

        if (method2 == null) {
            return classForMethod1;
        }

        String classForMethod2 = create
            .select(METHODS.CLASS_ID)
            .from(METHODS)
            .where(METHODS.METHOD_ID.eq(method2))
            .fetchInto(String.class)
            .get(0);

        if (classForMethod1.equals(classForMethod2)) {
            return classForMethod1;
        } else {
            return null;
        }
    }

    public String areBothFieldsFromSameClass(String field1, @Nullable String field2) {
        String classForField1 = create
            .select(FIELDS.CLASS_ID)
            .from(FIELDS)
            .where(FIELDS.FIELD_ID.eq(field1))
            .fetchInto(String.class)
            .get(0);

        if (field2 == null) {
            return classForField1;
        }

        String classForField2 = create
            .select(FIELDS.CLASS_ID)
            .from(FIELDS)
            .where(FIELDS.FIELD_ID.eq(field2))
            .fetchInto(String.class)
            .get(0);

        if (classForField1.equals(classForField2)) {
            return classForField1;
        } else {
            return null;
        }
    }
}
