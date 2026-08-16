package com.dlsu.medflow.model;

/**
 * The five actors defined in the project proposal, each with a distinct
 * scope of access (Statement of the Problem / System Framework).
 */
public enum Role {
    PATIENT("Patient"),
    DOCTOR("Doctor"),
    NURSE_STAFF("Nurse / Staff"),
    LAB_STAFF("Laboratory Staff"),
    ADMIN("Admin");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}


