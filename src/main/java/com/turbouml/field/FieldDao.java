package com.turbouml.field;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

import static org.jooq.schema.tables.Classes.CLASSES;
import static org.jooq.schema.tables.Fields.FIELDS;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.val;
import static org.jooq.schema.tables.Methods.METHODS;

@Repository("FieldDao")
public class FieldDao {
    private final DSLContext create;

    @Autowired
    public FieldDao(DSLContext create) {
        this.create = create;
    }

    public int getLastMethodInClass(String classId) {
        Integer highestOrder = create
            .select(max(FIELDS.CONTENT_ORDER))
            .from(FIELDS)
            .where(FIELDS.CLASS_ID.eq(classId))
            .fetchInto(Integer.class)
            .get(0);

        if (highestOrder == null) {
            highestOrder = 0;
        }
        return highestOrder;
    }

    public void save(FieldDto newField) {
        int highestOrder = getLastMethodInClass(newField.getClassId());

        create
            .insertInto(FIELDS,
                FIELDS.FIELD_ID,
                FIELDS.CONTENT_NAME,
                FIELDS.DATA_TYPE,
                FIELDS.ACCESS,
                FIELDS.STATIC,
                FIELDS.CLASS_ID,
                FIELDS.PROJECT_ID,
                FIELDS.CONTENT_ORDER
            )
            .select(
                create.select(
                        val(newField.getFieldId()),
                        val(newField.getContentName()),
                        val(newField.getDataType()),
                        val(newField.getAccess().name()),
                        val(newField.isStatic()),
                        val(newField.getClassId()),
                        CLASSES.PROJECT_ID,
                        val(highestOrder + 1)
                    ).from(CLASSES)
                    .where(CLASSES.CLASS_ID.eq(newField.getClassId()))
            )
            .execute();
    }

    public List<FieldDto> findByClassId(String classId) {
        return create.select()
            .from(FIELDS)
            .where(FIELDS.CLASS_ID.eq(classId))
            .orderBy(FIELDS.CONTENT_ORDER)
            .fetchInto(FieldDto.class);
    }

    public void update(FieldDto updatedField) {
        batchUpdate(
            updatedField,
            Collections.singletonList(
                updatedField.getFieldId()
            )
        );
    }

    public void batchUpdate(FieldDto updatedField, List<String> fieldIdsToUpdate) {
        create.update(METHODS)
            .set(FIELDS.CONTENT_NAME, updatedField.getContentName())
            .set(FIELDS.DATA_TYPE, updatedField.getDataType())
            .set(FIELDS.ACCESS, updatedField.getAccess().name())
            .set(FIELDS.STATIC, updatedField.isStatic())
            .where(FIELDS.FIELD_ID.in(fieldIdsToUpdate))
            .execute();
    }

    public void move(String fieldId, String fieldIdNewPos, String classIdForFields) {
        int newOrder = create
            .select(FIELDS.CONTENT_ORDER)
            .from(FIELDS)
            .where(FIELDS.FIELD_ID.eq(fieldIdNewPos))
            .fetchInto(Integer.class)
            .get(0);

        create.batch(
            create.update(FIELDS)
                .set(FIELDS.CONTENT_ORDER, FIELDS.CONTENT_ORDER.plus(1))
                .where(FIELDS.CLASS_ID.eq(classIdForFields))
                .and(FIELDS.CONTENT_ORDER.greaterOrEqual(newOrder)),
            create.update(FIELDS)
                .set(FIELDS.CONTENT_ORDER, newOrder)
                .where(FIELDS.FIELD_ID.eq(fieldId))
        ).execute();
    }

    public void moveToEnd(String methodId, String classId) {
        int newOrder = getLastMethodInClass(classId);

        create.update(FIELDS)
            .set(FIELDS.CONTENT_ORDER, newOrder)
            .where(FIELDS.FIELD_ID.eq(methodId))
            .execute();
    }

    public void delete(String fieldId) {
        create.deleteFrom(FIELDS)
            .where(FIELDS.FIELD_ID.eq(fieldId))
            .execute();
    }
}
