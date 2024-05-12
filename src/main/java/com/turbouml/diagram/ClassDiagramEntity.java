package com.turbouml.diagram;

import com.turbouml.field.FieldDto;
import com.turbouml.method.MethodEntity;
import com.turbouml.classuml.ClassDto;

import java.util.List;

/**
 * An entity to represent all the data in a single class on the diagram canvas,
 * stores the data of the class from the classes database table,
 * stores all the methods belonging to the class from the methods table,
 * stores all the fields belonging to the class from the fields table
 */
public class ClassDiagramEntity {
    private ClassDto umlClass;
    private List<MethodEntity> methods;
    private List<FieldDto> fields;

    public ClassDto getUmlClass() {
        return umlClass;
    }

    public void setUmlClass(ClassDto umlClass) {
        this.umlClass = umlClass;
    }

    public List<MethodEntity> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodEntity> methods) {
        this.methods = methods;
    }

    public List<FieldDto> getFields() {
        return fields;
    }

    public void setFields(List<FieldDto> fields) {
        this.fields = fields;
    }
}
