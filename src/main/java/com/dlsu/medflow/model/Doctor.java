package com.dlsu.medflow.model;

import com.dlsu.medflow.service.LabRoutingEngine;
import jakarta.persistence.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * INHERITANCE: child class of {@link User}. "Doctor - can view assigned
 * patients, add notes, and request lab tests" (System Framework).
 */
@Entity
public class Doctor extends User {

    private String specialization;

    // UNDERSTAND:
    // JPA requires a no-argument constructor when loading
    // Doctor objects from the database.
    protected Doctor() {
    }

    @Override
    public Parent displayDashboard(HospitalDataStore store) {
        return null;
    }

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

    @Override
    public void updateStatus(Visit visit, VisitStatus newStatus) {
        VisitStatus current = visit.getStatus();
        boolean allowed =
                (current == VisitStatus.ASSIGNED_TO_DOCTOR && newStatus == VisitStatus.UNDER_DOCTOR_ASSESSMENT)
                        || (current == VisitStatus.UNDER_DOCTOR_ASSESSMENT && newStatus == VisitStatus.LABORATORY_REQUESTED)
                        || (current == VisitStatus.FINDINGS_SENT_TO_DOCTOR && newStatus == VisitStatus.DOCTOR_REVIEWED)
                        || (current == VisitStatus.DOCTOR_REVIEWED && newStatus == VisitStatus.RELEASED_TO_PATIENT);

        if (!allowed) {
            throw new IllegalStateException(
                    "A doctor cannot move a visit from " + current.getLabel() + " to " + newStatus.getLabel() + ".");
        }
        visit.advance(this, newStatus);
    }

    // ---- POLYMORPHISM: method overloading -----------------------------------

    /**
     * "createRequest(String testName) - processes a single, standard
     * laboratory request." Always uses ROUTINE priority.
     */
    public LabRequest createRequest(Visit visit, String testName) {
        LabRequest request = buildRequest(testName, Priority.ROUTINE);
        visit.addLabRequest(request);
        advanceToRequested(visit);
        return request;
    }

    /**
     * "createRequest(String[] testNames, String priority) - processes a
     * bundled batch of tests flagged with a specific priority level (e.g.,
     * Urgent/Stat)." Same method name, different parameters - a second,
     * distinct laboratory-request pathway rather than a duplicate of the
     * single-test method above.
     */
    public List<LabRequest> createRequest(Visit visit, String[] testNames, String priority) {
        Priority parsedPriority;
        try {
            parsedPriority = Priority.valueOf(priority.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            parsedPriority = Priority.ROUTINE;
        }
        List<LabRequest> batch = new ArrayList<>();
        for (String testName : testNames) {
            if (testName == null || testName.isBlank()) {
                continue;
            }
            LabRequest request = buildRequest(testName.trim(), parsedPriority);
            visit.addLabRequest(request);
            batch.add(request);
        }
        advanceToRequested(visit);
        return batch;
    }

    private LabRequest buildRequest(String testName, Priority priority) {
        LabRequest request = new LabRequest(UUID.randomUUID().toString().substring(0, 8), testName, priority);
        request.setAssignedSection(LabRoutingEngine.routeTest(testName));
        return request;
    }

    private void advanceToRequested(Visit visit) {
        if (visit.getStatus() == VisitStatus.UNDER_DOCTOR_ASSESSMENT) {
            updateStatus(visit, VisitStatus.LABORATORY_REQUESTED);
        }
    }
}