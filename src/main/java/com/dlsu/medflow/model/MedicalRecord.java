package com.dlsu.medflow.model;

import java.io.Serializable;

/**
 * ENCAPSULATION: "diagnosis, doctor's notes, findings (private fields,
 * visible only to the assigned doctor or the patient)". The fields below are
 * private and can only be read through {@link #getDoctorNotes(User)} /
 * {@link #getDiagnosis(User, boolean)}, which check the requester's role
 * before returning anything. Anyone else is refused with a
 * {@link SecurityException} instead of silently receiving the data.
 */
// UNDERSTAND: MedicalRecord instances need to be persisted or transmitted across service boundaries.
// DECISION: Implement Serializable interface to support session state serialization and storage persistence.
public class MedicalRecord implements Serializable {

    // UNDERSTAND: Sensitive clinical data fields must be encapsulated and hidden from unauthorized direct access.
    // DECISION: Declare doctorNotes and diagnosis as private fields initialized to empty strings to avoid nulls.
    private String doctorNotes = "";
    private String diagnosis = "";

    /** Only the assigned doctor (or an Admin, for troubleshooting) may read the clinical notes. */
    // UNDERSTAND: Clinical notes contain sensitive diagnostic observations that require strict role-based authorization.
    // DECISION: Guard getDoctorNotes to allow access only to DOCTOR or ADMIN roles, throwing SecurityException otherwise.
    public String getDoctorNotes(User requester) {
        if (requester.getRole() == Role.DOCTOR || requester.getRole() == Role.ADMIN) {
            return doctorNotes;
        }
        throw new SecurityException("Only the assigned doctor or an admin may view clinical notes.");
    }

    // UNDERSTAND: Recording or modifying doctor notes must be restricted to clinical or administrative personnel.
    // DECISION: Validate requester role in setDoctorNotes and safely sanitize null input strings to empty defaults.
    public void setDoctorNotes(User requester, String doctorNotes) {
        if (requester.getRole() != Role.DOCTOR && requester.getRole() != Role.ADMIN) {
            throw new SecurityException("Only a doctor may record clinical notes.");
        }
        this.doctorNotes = doctorNotes == null ? "" : doctorNotes;
    }

    /**
     * The diagnosis is visible to the doctor/admin at any time, but only
     * visible to the patient once the visit has actually reached
     * {@code RELEASED_TO_PATIENT} - matching the proposal's requirement that
     * results only become visible to the patient once released.
     */
    // UNDERSTAND: Patient access to diagnosis must be conditional based on the visit's release status lifecycle stage.
    // DECISION: Grant immediate access to DOCTOR/ADMIN, but require releasedToPatient flag true for PATIENT requesters.
    public String getDiagnosis(User requester, boolean releasedToPatient) {
        if (requester.getRole() == Role.DOCTOR || requester.getRole() == Role.ADMIN) {
            return diagnosis;
        }
        if (requester.getRole() == Role.PATIENT && releasedToPatient) {
            return diagnosis;
        }
        throw new SecurityException("This result has not been released yet.");
    }

    // UNDERSTAND: Finalizing a medical diagnosis requires authoritative clinical or administrative access.
    // DECISION: Verify requester role in setDiagnosis, treating null values as empty strings to maintain data integrity.
    public void setDiagnosis(User requester, String diagnosis) {
        if (requester.getRole() != Role.DOCTOR && requester.getRole() != Role.ADMIN) {
            throw new SecurityException("Only a doctor may finalize a diagnosis.");
        }
        this.diagnosis = diagnosis == null ? "" : diagnosis;
    }

    // UNDERSTAND: System components need a quick mechanism to check if a diagnosis has been recorded.
    // DECISION: Provide hasDiagnosis utility method returning true when diagnosis is neither null nor blank.
    public boolean hasDiagnosis() {
        return diagnosis != null && !diagnosis.isBlank();
    }
}