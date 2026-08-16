package com.dlsu.medflow.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single patient visit as it moves through the 10-stage
 * lifecycle in the System Framework (Registered -&gt; ... -&gt; Released to
 * Patient).
 *
 * <p>ENCAPSULATION: "Visit - currentStatus (private field, changeable only
 * through a validated method)". The {@code status} field has no public
 * setter at all; the only way to change it is {@link #advance(User, VisitStatus)},
 * which every {@link User} subclass calls from inside its own overridden
 * {@link User#updateStatus(Visit, VisitStatus)} - meaning the role check
 * always happens before this method is ever reached. Unchanged from the
 * JavaFX edition — this class never depended on the UI framework at all.</p>
 */
// UNDERSTAND: Visit tracks a patient's medical journey across system workflow states.
// DECISION: Implement Serializable to allow persistence and session transmission of visit state.
public class Visit implements Serializable {

    // UNDERSTAND: Core identity, patient link, visit reason, and initial registration time are immutable.
    // DECISION: Declare private final fields for visitId, patient, reasonForVisit, and registeredAt.
    private final String visitId;
    private final Patient patient;
    private final String reasonForVisit;
    private final LocalDateTime registeredAt;

    // UNDERSTAND: Workflow status, doctor assignments, medical record, lab requests, and audit logs evolve over time.
    // DECISION: Initialize status to REGISTERED, instantiate final nested medical record, lab requests, and history log.
    private VisitStatus status = VisitStatus.REGISTERED;
    private Doctor recommendedDoctor;
    private Doctor assignedDoctor;
    private final MedicalRecord medicalRecord = new MedicalRecord();
    private final List<LabRequest> labRequests = new ArrayList<>();
    private final List<StatusLogEntry> history = new ArrayList<>();

    // UNDERSTAND: Constructing a visit requires binding the patient and visit reason while capturing creation timestamp.
    // DECISION: Assign identity properties, set registeredAt to current time, and record initial REGISTERED log entry.
    public Visit(String visitId, Patient patient, String reasonForVisit) {
        this.visitId = visitId;
        this.patient = patient;
        this.reasonForVisit = reasonForVisit;
        this.registeredAt = LocalDateTime.now();
        this.history.add(new StatusLogEntry(VisitStatus.REGISTERED, registeredAt));
    }

    /**
     * The only way {@link #status} is ever mutated. Callers are always a
     * {@link User} subclass's already-validated {@code updateStatus(...)}
     * override, never controller code directly.
     */
    // UNDERSTAND: Mutating status during live user interactions defaults to the current timestamp.
    // DECISION: Delegate advance call with current LocalDateTime to the overloaded advance method.
    public void advance(User approver, VisitStatus newStatus) {
        advance(approver, newStatus, LocalDateTime.now());
    }

    /**
     * Overload used when a precise historical timestamp matters (currently
     * only the demo-data seeding in {@code HospitalDataStore}, so sample
     * visits show a realistic timeline instead of every step happening in
     * the same instant).
     */
    // UNDERSTAND: Workflow status transitions must verify user authorization and record audit entry timestamps.
    // DECISION: Throw SecurityException if approver is null, then update status field and append StatusLogEntry to history.
    public void advance(User approver, VisitStatus newStatus, LocalDateTime when) {
        if (approver == null) {
            throw new SecurityException("A visit's status can only change through a validated user action.");
        }
        this.status = newStatus;
        this.history.add(new StatusLogEntry(newStatus, when));
    }
}