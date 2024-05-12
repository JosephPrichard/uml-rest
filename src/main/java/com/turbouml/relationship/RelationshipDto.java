package com.turbouml.relationship;

/**
 * Represents an entity on the relationships database table
 * A single relationship is a vertex
 * The table is a vertex list (the best way to store a graph in a relational database)
 */
public class RelationshipDto {
    private String relationshipId;
    private String classIdFrom;
    private String classIdTo;
    private RelationshipType type;
    private String label;
    private String projectId;

    public String getRelationshipId() {
        return relationshipId;
    }

    public void setRelationshipId(String relationshipId) {
        this.relationshipId = relationshipId;
    }

    public String getClassIdFrom() {
        return classIdFrom;
    }

    public void setClassIdFrom(String classIdFrom) {
        this.classIdFrom = classIdFrom;
    }

    public String getClassIdTo() {
        return classIdTo;
    }

    public void setClassIdTo(String classIdTo) {
        this.classIdTo = classIdTo;
    }

    public RelationshipType getType() {
        return type;
    }

    public void setType(RelationshipType type) {
        this.type = type;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
