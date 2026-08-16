package com.dlsu.medflow.model;

import com.dlsu.medflow.service.HospitalDataStore;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * INHERITANCE: child class of {@link User}. "Admin - can manage user
 * accounts, doctor categories, and laboratory sections" (System Framework).
 */
public class Admin extends User {

    // UNDERSTAND: Admin requires a concrete sub-class implementation of User with explicit Role assignment.
    // DECISION: Pass Role.ADMIN directly to super constructor to enforce fixed role classification.
    public Admin(String userId, String name, String username, String password) {
        super(userId, name, username, password, Role.ADMIN);
    }

    // UNDERSTAND: Framework routing needs to identify which UI dashboard layout to render for admins.
    // DECISION: Hardcode relative view path string to map directly to the admin template file.
    @Override
    public String getDashboardView() {
        return "admin/dashboard";
    }

    @Override
    public Map<String, Object> buildDashboardModel(HospitalDataStore store) {
        // UNDERSTAND: Dashboard overview needs metric counters for system user roles and active clinical visits.
        // DECISION: Stream and filter all system users and visits in real-time to compute aggregate counts.
        long patients = store.getAllUsers().stream().filter(u -> u.getRole() == Role.PATIENT).count();
        long doctors = store.getAllUsers().stream().filter(u -> u.getRole() == Role.DOCTOR).count();
        long staff = store.getAllUsers().stream()
                .filter(u -> u.getRole() == Role.NURSE_STAFF || u.getRole() == Role.LAB_STAFF).count();