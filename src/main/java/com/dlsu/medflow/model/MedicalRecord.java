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
}