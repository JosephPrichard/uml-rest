package com.turbouml.classuml;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.jooq.schema.Tables.*;

@Repository("ClassDao")
public class ClassDao {
    private final DSLContext create;

    @Autowired
    public ClassDao(DSLContext create) {
        this.create = create;
    }

    public void save(ClassDto newClass) {
        create
            .insertInto(CLASSES,
                CLASSES.CLASS_ID,
                CLASSES.CONTENT_NAME,
                CLASSES.ACCESS,
                CLASSES.STEREOTYPES,
                CLASSES.PROJECT_ID,
                CLASSES.X_POS,
                CLASSES.Y_POS
            )
            .values(
                newClass.getClassId(),
                newClass.getContentName(),
                newClass.getAccess().name(),
                newClass.getStereotypes(),
                newClass.getProjectId(),
                newClass.getXPos(),
                newClass.getYPos()
            )
            .execute();
    }

    public ClassDto findByClassId(String classId) {
        return create.select()
            .from(CLASSES)
            .where(CLASSES.CLASS_ID.eq(classId))
            .fetchInto(ClassDto.class)
            .get(0);
    }

    public List<ClassDto> findByProjectId(String projectId) {
        return create.select()
            .from(CLASSES)
            .where(CLASSES.PROJECT_ID.eq(projectId))
            .fetchInto(ClassDto.class);
    }

    public void updateStereotypes(String classId, String[] stereotypes) {
        create.update(CLASSES)
            .set(CLASSES.STEREOTYPES, stereotypes)
            .where(CLASSES.CLASS_ID.eq(classId))
            .execute();
    }

    public void rename(String classId, String newName) {
        create.update(CLASSES)
            .set(CLASSES.CONTENT_NAME, newName)
            .where(CLASSES.CLASS_ID.eq(classId))
            .execute();
    }

    public void move(String classId, int x, int y) {
        create.update(CLASSES)
            .set(CLASSES.X_POS, x)
            .set(CLASSES.Y_POS, y)
            .where(CLASSES.CLASS_ID.eq(classId))
            .execute();
    }

    public void delete(String classId) {
        create.deleteFrom(CLASSES)
            .where(CLASSES.CLASS_ID.eq(classId))
            .execute();
    }

    public void delete(List<String> classIds) {
        create.deleteFrom(CLASSES)
            .where(CLASSES.CLASS_ID.in(classIds))
            .execute();
    }
}
