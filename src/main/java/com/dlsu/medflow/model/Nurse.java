package com.dlsu.medflow.model;

import com.dlsu.medflow.service.HospitalDataStore;

import java.util.HashMap;
import java.util.Map;

/**
 * INHERITANCE: child class of {@link User}. "Nurse/Staff - can confirm
 * registration and sample collection" (System Framework).
 */
public class Nurse extends User {

    // UNDERSTAND: Nurse subclass represents clinical staff handling patient onboarding, triage, and specimen collection.
    // DECISION: Pass credentials to super constructor with fixed role assignment to Role.NURSE_STAFF.
    public Nurse(String userId, String name, String username, String password) {
        super(userId, name, username, password, Role.NURSE_STAFF);
    }

    // UNDERSTAND: Web framework maps Nurse users to their dedicated dashboard template.
    // DECISION: Return explicit relative path string pointing directly to nurse dashboard view.
    @Override
    public String getDashboardView() {
        return "nurse/dashboard";
    }

    // UNDERSTAND: Nurse dashboard requires comprehensive queues across registration, sampling, lab transit, and doctor assignment.
    // DECISION: Query HospitalDataStore for pending queues and doctor lists to populate dashboard model map.
    @Override
    public Map<String, Object> buildDashboardModel(HospitalDataStore store) {
        Map<String, Object> model = new HashMap<>();
        model.put("registrations", store.getPendingRegistrations());
}