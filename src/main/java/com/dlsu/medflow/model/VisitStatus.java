package com.dlsu.medflow.model;

import java.io.Serializable;

/**
 * The 10-stage visit lifecycle defined in the proposal's "Status Descriptions"
 * table, from Registered to Released to Patient. The declaration order below
 * IS the workflow order, so ordinal() doubles as the step index used by
 * {@link com.dlsu.medflow.gui.components.StatusTrackerView}.
 */
public enum VisitStatus implements Serializable {

    REGISTERED(
            "Registered",
            "Patient record is created in the system."),
    ASSIGNED_TO_DOCTOR(
            "Assigned to the Doctor",
            "The system recommends a doctor; Nurse/Staff confirms the assignment."),
    UNDER_DOCTOR_ASSESSMENT(
            "Under Doctor Assessment",
            "The doctor examines the patient and records notes."),
    LABORATORY_REQUESTED(
            "Laboratory Requested",
            "The doctor creates a laboratory request for the visit."),
    SAMPLE_COLLECTED(
            "Sample Collected",
            "Nurse/Staff confirms that the required sample has been collected."),
    SENT_TO_LABORATORY(
            "Sent to the Laboratory",
            "The request and sample are forwarded to the laboratory."),
    UNDER_LABORATORY_ANALYSIS(
            "Under Laboratory Analysis",
            "The request is routed to the proper laboratory section for processing."),
    FINDINGS_SENT_TO_DOCTOR(
            "Findings Sent to Doctor",
            "Laboratory staff finish encoding findings for the doctor's review."),
    DOCTOR_REVIEWED(
            "Doctor Reviewed",
            "The doctor reviews and confirms the laboratory findings."),
    RELEASED_TO_PATIENT(
            "Released to Patient",
            "The final result is released and made visible to the patient.");

    private final String label;
    private final String description;

    VisitStatus(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    /** 1-based stage number, matching the proposal's "Status #" column. */
    public int getStageNumber() {
        return ordinal() + 1;
    }
}