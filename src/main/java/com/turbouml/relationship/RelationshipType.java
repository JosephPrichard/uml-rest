package com.turbouml.relationship;

public enum RelationshipType {
    INHERITANCE, REALIZATION, ASSOCIATION, AGGREGATION, COMPOSITION, DEPENDENCY;

    public static RelationshipType stringToEnum(String type) {
        return switch (type) {
            case "INHERITANCE" -> INHERITANCE;
            case "REALIZATION" -> REALIZATION;
            case "ASSOCIATION" -> ASSOCIATION;
            case "AGGREGATION" -> AGGREGATION;
            case "COMPOSITION" -> COMPOSITION;
            case "DEPENDENCY" -> DEPENDENCY;
            default -> throw new IllegalStateException("Missing switch case");
        };
    }
}
