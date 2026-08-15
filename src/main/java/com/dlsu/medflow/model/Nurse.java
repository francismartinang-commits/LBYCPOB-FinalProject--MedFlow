package com.dlsu.medflow.model;

import jakarta.persistence.Entity;

/**
 * INHERITANCE: child class of {@link User}. "Nurse/Staff - can confirm
 * registration and sample collection" (System Framework).
 */
@Entity
public class Nurse extends User {

    // UNDERSTAND:
    // JPA requires a no-argument constructor when loading
    // nurse accounts from the database.
    protected Nurse() {
    }

    public Nurse(String userId, String name, String username, String password) {
        super(userId, name, username, password, Role.NURSE_STAFF);
    }

    @Override
    public void updateStatus(Visit visit, VisitStatus newStatus) {
        VisitStatus current = visit.getStatus();
        boolean allowed =
                (current == VisitStatus.REGISTERED && newStatus == VisitStatus.ASSIGNED_TO_DOCTOR)
                        || (current == VisitStatus.LABORATORY_REQUESTED && newStatus == VisitStatus.SAMPLE_COLLECTED)
                        || (current == VisitStatus.SAMPLE_COLLECTED && newStatus == VisitStatus.SENT_TO_LABORATORY)
                        || (current == VisitStatus.SENT_TO_LABORATORY && newStatus == VisitStatus.UNDER_LABORATORY_ANALYSIS);

        if (!allowed) {
            throw new IllegalStateException(
                    "Nurse/Staff cannot move a visit from " + current.getLabel() + " to " + newStatus.getLabel() + ".");
        }
        visit.advance(this, newStatus);
    }
}