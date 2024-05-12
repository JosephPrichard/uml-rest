package com.turbouml.method;

import com.turbouml.classuml.Access;

/**
 * Represents an entity on the methods database table
 */
public class MethodEntity {
    private String methodId;
    private String contentName;
    private String returnType; //can be null
    private String params; //doesn't need to adhere to any format
    private Access access;
    private boolean isStatic = false;
    private boolean isAbstract = false;
    private String classId;
    private String projectId; //can be null for insertion

    public String getMethodId() {
        return methodId;
    }

    public void setMethodId(String methodId) {
        this.methodId = methodId;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public Access getAccess() {
        return access;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean anAbstract) {
        isAbstract = anAbstract;
    }
}
