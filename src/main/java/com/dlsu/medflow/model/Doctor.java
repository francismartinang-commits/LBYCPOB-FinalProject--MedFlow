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

    // UNDERSTAND: UI display components need extended role context for doctors.
    // DECISION: Override getRoleDetail to dynamically append specialization to the base role title.
    @Override
    public String getRoleDetail() {
        return "Doctor - " + specialization;
    }

    // UNDERSTAND: Framework routing maps Doctor users to their designated web template.
    // DECISION: Return explicit relative path string mapping directly to doctor dashboard view.
    @Override
    public String getDashboardView() {
        return "doctor/dashboard";
    }

    @Override
    public Map<String, Object> buildDashboardModel(HospitalDataStore store) {
        // UNDERSTAND: Doctor dashboard requires active visits prioritized above completed/released visits.
        // DECISION: Sort patient visit list using Boolean comparison so RELEASED_TO_PATIENT visits sink to bottom.
        List<Visit> visits = new ArrayList<>(store.getVisitsForDoctor(this));
        visits.sort((a, b) -> Boolean.compare(
                a.getStatus() == VisitStatus.RELEASED_TO_PATIENT,
                b.getStatus() == VisitStatus.RELEASED_TO_PATIENT));