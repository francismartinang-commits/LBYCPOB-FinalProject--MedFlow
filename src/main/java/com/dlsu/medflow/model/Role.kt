package com.dlsu.medflow.model

enum class Role(displayName: String) {
    PATIENT("Patient"),
    DOCTOR("Doctor"),
    NURSE_STAFF("Nurse / Staff"),
    LAB_STAFF("Laboratory Staff"),
    ADMIN("Admin");

    val displayName: String?

    init {
        this.displayName = displayName
    }
}