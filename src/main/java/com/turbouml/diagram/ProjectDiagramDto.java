package com.turbouml.diagram;

import com.turbouml.project.ProjectDto;
import com.turbouml.packageuml.PackageDto;
import com.turbouml.relationship.RelationshipDto;

import java.util.List;

/**
 * An entity to represent all the data in a single diagram for a project
 * stores the data of the project from the project database table,
 * stores all class diagrams belonging to the project,
 * stores all relationships belonging to the project
 * stores all packages belonging to the project
 * stores the dimensions of the diagram canvas
 */
public class ProjectDiagramDto {
    public static final int MAX_X = 2000;
    public static final int MAX_Y = 2000;

    private ProjectDto project;
    private List<ClassDiagramEntity> classDiagrams;
    private List<RelationshipDto> classRelationships;
    private List<PackageDto> packages;
    private int maxX = MAX_X;
    private int maxY = MAX_Y;

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public ProjectDto getProject() {
        return project;
    }

    public void setProject(ProjectDto project) {
        this.project = project;
    }

    public List<RelationshipDto> getClassRelationships() {
        return classRelationships;
    }

    public void setClassRelationships(List<RelationshipDto> classRelationships) {
        this.classRelationships = classRelationships;
    }

    public List<PackageDto> getPackages() {
        return packages;
    }

    public void setPackages(List<PackageDto> packages) {
        this.packages = packages;
    }

    public List<ClassDiagramEntity> getClassDiagrams() {
        return classDiagrams;
    }

    public void setClassDiagrams(List<ClassDiagramEntity> classDiagrams) {
        this.classDiagrams = classDiagrams;
    }
}
