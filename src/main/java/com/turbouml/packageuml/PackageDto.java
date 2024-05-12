package com.turbouml.packageuml;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an entity on the packages database table
 */
public class PackageDto {
    private String packageId;
    @JsonProperty("xPos")
    private int xPos;
    @JsonProperty("yPos")
    private int yPos;
    @JsonProperty("xDist")
    private int xDist;
    @JsonProperty("yDist")
    private int yDist;
    private String projectId;
    private String contentName;

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
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

    public int getXDist() {
        return xDist;
    }

    public void setXDist(int xDist) {
        this.xDist = xDist;
    }

    public int getYDist() {
        return yDist;
    }

    public void setYDist(int yDist) {
        this.yDist = yDist;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }
}
