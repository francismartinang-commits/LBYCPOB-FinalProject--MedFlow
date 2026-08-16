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

        // UNDERSTAND: Summary cards require counts across various actionable stage groupings.
        // DECISION: Stream visits to filter metrics for assessment, lab findings, overall progress, and release status.
        long awaitingAssessment = visits.stream().filter(v -> v.getStatus() == VisitStatus.ASSIGNED_TO_DOCTOR).count();
        long awaitingFindings = visits.stream().filter(v -> v.getStatus() == VisitStatus.FINDINGS_SENT_TO_DOCTOR).count();
        long inProgress = visits.stream().filter(v -> v.getStatus() != VisitStatus.RELEASED_TO_PATIENT).count();
        long completed = visits.stream().filter(v -> v.getStatus() == VisitStatus.RELEASED_TO_PATIENT).count();

        Map<String, Object> model = new HashMap<>();
        model.put("visits", visits);
        model.put("awaitingAssessment", awaitingAssessment);
        model.put("awaitingFindings", awaitingFindings);
        model.put("inProgress", inProgress);
        model.put("completed", completed);
        return model;
    }

    @Override
    public void updateStatus(Visit visit, VisitStatus newStatus) {
        // UNDERSTAND: Doctors are restricted to specific, valid workflow state transitions.
        // DECISION: Enforce strict boolean rule matrix allowing only authorized status transitions, throwing exception otherwise.
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
    // Unchanged from the JavaFX edition: neither overload ever touched the UI
    // layer, so both survive the conversion to Spring Boot verbatim.

    /**
     * "createRequest(String testName) - processes a single, standard
     * laboratory request." Always uses ROUTINE priority.
     */
    // UNDERSTAND: Single lab requests are common routine occurrences.
    // DECISION: Overload createRequest to default single test ordering directly to Priority.ROUTINE.
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
    // UNDERSTAND: Multiple lab tests need batch creation under a user-selected priority level.
    // DECISION: Overload createRequest with array parameter, parsing priority safely with ROUTINE fallback.
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