package com.turbouml.project;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.jooq.schema.Tables.PROJECTS;

@Repository("ProjectDao")
public class ProjectDao {
    private final DSLContext create;

    @Autowired
    public ProjectDao(DSLContext create) {
        this.create = create;
    }

    public void save(ProjectDto newProject) {
        create
            .insertInto(PROJECTS,
                PROJECTS.PROJECT_ID,
                PROJECTS.CONTENT_NAME,
                PROJECTS.LANG,
                PROJECTS.USER_ID,
                PROJECTS.TIMESTAMP
            )
            .values(
                newProject.getProjectId(),
                newProject.getContentName(),
                newProject.getLang().name(),
                newProject.getUserId(),
                newProject.getTimestamp()
            )
            .execute();
    }

    public ProjectDto findProjectById(String projectId) {
        return create.select()
            .from(PROJECTS)
            .where(PROJECTS.PROJECT_ID.eq(projectId))
            .fetchInto(ProjectDto.class)
            .get(0);
    }

    public List<ProjectDto> findProjectsForUser(String userId) {
        return create.select()
            .from(PROJECTS)
            .where(PROJECTS.USER_ID.eq(userId))
            .fetchInto(ProjectDto.class);
    }

    public void rename(String projectId, String newName) {
        create.update(PROJECTS)
            .set(PROJECTS.CONTENT_NAME, newName)
            .where(PROJECTS.PROJECT_ID.eq(projectId))
            .execute();
    }

    public void delete(String projectId) {
        create.deleteFrom(PROJECTS)
            .where(PROJECTS.PROJECT_ID.eq(projectId))
            .execute();
    }
}