package com.dlsu.medflow.model;

import com.dlsu.medflow.service.HospitalDataStore;

import java.util.HashMap;
import java.util.Map;

/**
 * INHERITANCE: child class of {@link User}. "Laboratory Staff - can view lab
 * requests and encode findings" (System Framework).
 */
public class LabStaff extends User {

    private String section;

    // UNDERSTAND: LabStaff extends User with section-specific routing metadata.
    // DECISION: Store section field alongside base User credentials and fix role to Role.LAB_STAFF.
    public LabStaff(String userId, String name, String username, String password, String section) {
        super(userId, name, username, password, Role.LAB_STAFF);
        this.section = section;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    // UNDERSTAND: UI components need detailed role context including section assignment.
    // DECISION: Override getRoleDetail to append assigned section to base laboratory staff title.
    @Override
    public String getRoleDetail() {
        return "Laboratory Staff - " + section;
    }
}