package com.dlsu.medflow.model

enum class Role(val displayName: String) {
    PATIENT("Patient"),
    DOCTOR("Doctor"),
    NURSE_STAFF("Nurse / Staff"),
    LAB_STAFF("Laboratory Staff"),
    ADMIN("Admin")
}