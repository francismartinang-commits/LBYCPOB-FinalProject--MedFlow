package com.dlsu.medflow.model;

import jakarta.persistence.Entity;

/**
 * INHERITANCE: child class of {@link User}. "Laboratory Staff - can view lab
 * requests and encode findings" (System Framework).
 */
@Entity
public class LabStaff extends User {

    private String section;

    // UNDERSTAND:
    // JPA requires a no-argument constructor when loading
    // laboratory staff accounts from the database.
    protected LabStaff() {
    }

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

    @Override
    public void updateStatus(Visit visit, VisitStatus newStatus) {
        VisitStatus current = visit.getStatus();

        if (current == VisitStatus.UNDER_LABORATORY_ANALYSIS
                && newStatus == VisitStatus.FINDINGS_SENT_TO_DOCTOR) {

            if (!visit.allFindingsEncoded()) {
                throw new IllegalStateException(
                        "All test findings must be encoded before this visit can move on."
                );
            }

            visit.advance(this, newStatus);
            return;
        }

        throw new IllegalStateException(
                "Laboratory Staff cannot move a visit from "
                        + current.getLabel()
                        + " to "
                        + newStatus.getLabel()
                        + "."
        );
    }
}