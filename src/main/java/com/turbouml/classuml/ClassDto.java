package com.turbouml.classuml;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import static com.turbouml.diagram.ProjectDiagramDto.MAX_X;
import static com.turbouml.diagram.ProjectDiagramDto.MAX_Y;

/**
 * Represents an entity on the classes database table
 */
public class ClassDto {
    private String classId;
    private Access access;
    private String[] stereotypes;
    private String projectId;

    @Size(min = 4, max = 30)
    private String contentName;

    @Min(0)
    @Max(MAX_X)
    private int xPos;

    @Min(0)
    @Max(MAX_Y)
    private int yPos;

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public String[] getStereotypes() {
        return stereotypes;
    }

    public void setStereotypes(String[] stereotypes) {
        this.stereotypes = stereotypes;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public int getXPos() {
        return xPos;
    }

    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public void setYPos(int yPos) {
        this.yPos = yPos;
    }
}
