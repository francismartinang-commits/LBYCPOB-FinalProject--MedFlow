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

    // UNDERSTAND: Web framework routes lab staff users to their designated view template.
    // DECISION: Return relative template path pointing directly to lab staff dashboard view.
    @Override
    public String getDashboardView() {
        return "labstaff/dashboard";
    }

    // UNDERSTAND: Lab staff dashboard requires current section queue items and pending count.
    // DECISION: Query HospitalDataStore using staff section and expose items and size in model map.
    @Override
    public Map<String, Object> buildDashboardModel(HospitalDataStore store) {
        var pending = store.getPendingLabRequests(section);
        Map<String, Object> model = new HashMap<>();
        model.put("queueItems", pending);
        model.put("pendingCount", pending.size());
        return model;
    }

    @Override
    public void updateStatus(Visit visit, VisitStatus newStatus) {
        // UNDERSTAND: Lab staff can only advance visits from analysis to completed findings stage.
        // DECISION: Restrict status transitions strictly to UNDER_LABORATORY_ANALYSIS -> FINDINGS_SENT_TO_DOCTOR.
        VisitStatus current = visit.getStatus();
        if (current == VisitStatus.UNDER_LABORATORY_ANALYSIS && newStatus == VisitStatus.FINDINGS_SENT_TO_DOCTOR) {

            // UNDERSTAND: Advancing to doctor review requires all pending lab tests to have encoded results.
            // DECISION: Throw IllegalStateException if visit.allFindingsEncoded() evaluates to false.
            if (!visit.allFindingsEncoded()) {
                throw new IllegalStateException("All test findings must be encoded before this visit can move on.");
            }
            visit.advance(this, newStatus);
            return;
        }
        }
}