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

    /** True once every lab request attached to this visit has encoded findings. */
    // UNDERSTAND: Progressing beyond lab stages requires verifying that all associated lab requests have encoded findings.
    // DECISION: Return false if labRequests is empty or if any request is unencoded; return true if all are encoded.
    public boolean allFindingsEncoded() {
        if (labRequests.isEmpty()) {
            return false;
        }
        for (LabRequest request : labRequests) {
            if (!request.isFindingsEncoded()) {
                return false;
            }
        }
        return true;
    }

    // UNDERSTAND: Medical testing orders must be attached to the visit instance.
    // DECISION: Append given LabRequest to the internal labRequests collection.
    public void addLabRequest(LabRequest request) {
        labRequests.add(request);
    }

    /**
     * The timestamp this visit first reached {@code stage}, formatted for
     * display, or {@code null} if it hasn't reached that stage yet. Used by
     * the status-tracker Thymeleaf fragment; was a private method inside the
     * JavaFX {@code StatusTrackerView} before this conversion.
     */
    // UNDERSTAND: UI timeline components need to retrieve the exact timestamp when a visit entered a specific stage.
    // DECISION: Iterate history log, return matching timestamp for target status, or null if stage hasn't been reached.
    public LocalDateTime getTimestampFor(VisitStatus stage) {
        for (StatusLogEntry entry : history) {
            if (entry.getStatus() == stage) {
                return entry.getTimestamp();
            }
        }
        return null;
    }

    /**
     * Human-readable doctor line for a patient's visit card — "assigned",
     * "recommended, pending confirmation", or "not yet assigned". Moved here
     * from {@code PatientDashboard} so the template can stay simple.
     */
    // UNDERSTAND: UI cards require formatted strings summarizing assigned or recommended doctor statuses.
    // DECISION: Check assignedDoctor first, then recommendedDoctor, returning formatted descriptive text or default fallback string.
    public String getDoctorLabel() {
        if (assignedDoctor != null) {
            return assignedDoctor.getName() + " (" + assignedDoctor.getSpecialization() + ")";
        }
        if (recommendedDoctor != null) {
            return recommendedDoctor.getName() + " (recommended, pending confirmation)";
        }
        return "Not yet assigned";
    }

    // ---- getters ------------------------------------------------------------

    // UNDERSTAND: Encapsulated properties require controlled getter access and explicit mutators for doctor assignments.
    // DECISION: Expose standard getters for core properties and setters for recommendedDoctor and assignedDoctor.
    public String getVisitId() {
        return visitId;
    }

    public Patient getPatient() {
        return patient;
    }

    public String getReasonForVisit() {
        return reasonForVisit;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public VisitStatus getStatus() {
        return status;
    }

    public Doctor getRecommendedDoctor() {
        return recommendedDoctor;
    }

    public void setRecommendedDoctor(Doctor recommendedDoctor) {
        this.recommendedDoctor = recommendedDoctor;
    }

    public Doctor getAssignedDoctor() {
        return assignedDoctor;
    }

    public void setAssignedDoctor(Doctor assignedDoctor) {
        this.assignedDoctor = assignedDoctor;
    }

    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    public List<LabRequest> getLabRequests() {
        return labRequests;
    }

    public List<StatusLogEntry> getHistory() {
        return history;
    }

    // UNDERSTAND: Log statements and debug views need concise visual summaries of a visit instance.
    // DECISION: Override toString to output visitId, patient name, and status display label.
    @Override
    public String toString() {
        return "Visit " + visitId + " - " + patient.getName() + " (" + status.getLabel() + ")";
    }
}