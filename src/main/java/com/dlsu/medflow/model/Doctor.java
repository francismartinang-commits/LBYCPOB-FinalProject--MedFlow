package com.dlsu.medflow.model;

import com.dlsu.medflow.service.HospitalDataStore;
import com.dlsu.medflow.service.LabRoutingEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * INHERITANCE: child class of {@link User}. "Doctor - can view assigned
 * patients, add notes, and request lab tests" (System Framework).
 */
public class Doctor extends User {

    private String specialization;

    // UNDERSTAND: Doctor subclass extends User with role-specific clinical metadata.
    // DECISION: Store specialization field alongside standard User credentials with fixed Role.DOCTOR.
    public Doctor(String userId, String name, String username, String password, String specialization) {
        super(userId, name, username, password, Role.DOCTOR);
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }