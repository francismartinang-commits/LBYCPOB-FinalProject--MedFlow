package com.dlsu.medflow.model;

import com.dlsu.medflow.gui.doctor.DoctorDashboard;
import com.dlsu.medflow.service.HospitalDataStore;
import com.dlsu.medflow.service.LabRoutingEngine;
import javafx.scene.Parent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Doctor extends User {

    private String specialization;

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
    public Parent displayDashboard(HospitalDataStore store) {
        return new DoctorDashboard(this, store);
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

    // ---- POLYMORPHISM: method overloading ----

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
