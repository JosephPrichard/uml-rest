package com.turbouml.method;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

import static org.jooq.schema.tables.Classes.CLASSES;
import static org.jooq.schema.tables.Methods.METHODS;
import static org.jooq.impl.DSL.*;

@Repository("MethodDao")
public class MethodDao {
    private final DSLContext create;

    @Autowired
    public MethodDao(DSLContext create) {
        this.create = create;
    }

    public int getLastMethodInClass(String classId) {
        Integer highestOrder = create
            .select(max(METHODS.CONTENT_ORDER))
            .from(METHODS)
            .where(METHODS.CLASS_ID.eq(classId))
            .fetchInto(Integer.class)
            .get(0);

        if (highestOrder == null) {
            highestOrder = 0;
        }
        return highestOrder;
    }

    public void save(MethodEntity newMethod) {
        int highestOrder = getLastMethodInClass(newMethod.getClassId());

        create
            .insertInto(METHODS,
                METHODS.METHOD_ID,
                METHODS.CONTENT_NAME,
                METHODS.PARAMS,
                METHODS.RETURN_TYPE,
                METHODS.ACCESS,
                METHODS.STATIC,
                METHODS.ABSTRACT,
                METHODS.CLASS_ID,
                METHODS.PROJECT_ID,
                METHODS.CONTENT_ORDER
            )
            .select(
                create.select(
                        val(newMethod.getMethodId()),
                        val(newMethod.getContentName()),
                        val(newMethod.getParams()),
                        val(newMethod.getReturnType()),
                        val(newMethod.getAccess().name()),
                        val(newMethod.isStatic()),
                        val(newMethod.isAbstract()),
                        val(newMethod.getClassId()),
                        CLASSES.PROJECT_ID,
                        val(highestOrder + 1)
                    ).from(CLASSES)
                    .where(CLASSES.CLASS_ID.eq(newMethod.getClassId()))
            )
            .execute();
    }

    public List<MethodEntity> findByClassId(String classId) {
        return create.select()
            .from(METHODS)
            .where(METHODS.CLASS_ID.eq(classId))
            .orderBy(METHODS.CONTENT_ORDER)
            .fetchInto(MethodEntity.class);
    }

    public void update(MethodEntity updatedMethod) {
        batchUpdate(
            updatedMethod,
            Collections.singletonList(
                updatedMethod.getMethodId()
            )
        );
    }

    public void batchUpdate(MethodEntity updatedMethod, List<String> methodIdsToUpdate) {
        create.update(METHODS)
            .set(METHODS.CONTENT_NAME, updatedMethod.getContentName())
            .set(METHODS.RETURN_TYPE, updatedMethod.getReturnType())
            .set(METHODS.PARAMS, updatedMethod.getParams())
            .set(METHODS.ACCESS, updatedMethod.getAccess().name())
            .set(METHODS.STATIC, updatedMethod.isStatic())
            .set(METHODS.ABSTRACT, updatedMethod.isAbstract())
            .where(METHODS.METHOD_ID.in(methodIdsToUpdate))
            .execute();
    }

    public void move(String methodId, String methodIdNewPos, String classIdForMethods) {
        int newOrder = create
            .select(METHODS.CONTENT_ORDER)
            .from(METHODS)
            .where(METHODS.METHOD_ID.eq(methodIdNewPos))
            .fetchInto(Integer.class)
            .get(0);

        create.batch(
            create.update(METHODS)
                .set(METHODS.CONTENT_ORDER, METHODS.CONTENT_ORDER.plus(1))
                .where(METHODS.CLASS_ID.eq(classIdForMethods))
                .and(METHODS.CONTENT_ORDER.greaterOrEqual(newOrder)),
            create.update(METHODS)
                .set(METHODS.CONTENT_ORDER, newOrder)
                .where(METHODS.METHOD_ID.eq(methodId))
        ).execute();
    }

    public void moveToEnd(String methodId, String classId) {
        int newOrder = getLastMethodInClass(classId);

        create.update(METHODS)
            .set(METHODS.CONTENT_ORDER, newOrder)
            .where(METHODS.METHOD_ID.eq(methodId))
            .execute();
    }

    public void delete(String methodId) {
        create.deleteFrom(METHODS)
            .where(METHODS.METHOD_ID.eq(methodId))
            .execute();
    }
}
