package com.turbouml.classuml;

public enum Access {
    PUBLIC, PRIVATE, PROTECTED, PACKAGE_PRIVATE, INTERNAL;

    public static Access stringToEnum(String access) {
        return switch (access) {
            case "PUBLIC" -> PUBLIC;
            case "PRIVATE" -> PRIVATE;
            case "PROTECTED" -> PROTECTED;
            case "PACKAGE_PRIVATE" -> PACKAGE_PRIVATE;
            case "INTERNAL" -> INTERNAL;
            default -> throw new IllegalStateException("Missing switch case");
        };
    }
}
