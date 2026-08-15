package com.dlsu.medflow.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

/**
 * ENCAPSULATION: "diagnosis, doctor's notes, findings (private fields,
 * visible only to the assigned doctor or the patient)". The fields below are
 * private and can only be read through {@link #getDoctorNotes(User)} /
 * {@link #getDiagnosis(User, boolean)}, which check the requester's role
 * before returning anything. Anyone else is refused with a
 * {@link SecurityException} instead of silently receiving the data.
 */
@Embeddable
public class MedicalRecord implements Serializable {

    private String doctorNotes = "";
    private String diagnosis = "";

    // UNDERSTAND:
    // JPA can store this object as part of the Visit entity
    // instead of creating a separate medical record table.
    protected MedicalRecord() {
    }

    /** Only the assigned doctor (or an Admin, for troubleshooting) may read the clinical notes. */
    public String getDoctorNotes(User requester) {
        if (requester.getRole() == Role.DOCTOR || requester.getRole() == Role.ADMIN) {
            return doctorNotes;
        }
        throw new SecurityException("Only the assigned doctor or an admin may view clinical notes.");
    }

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
    public String getDiagnosis(User requester, boolean releasedToPatient) {
        if (requester.getRole() == Role.DOCTOR || requester.getRole() == Role.ADMIN) {
            return diagnosis;
        }
        if (requester.getRole() == Role.PATIENT && releasedToPatient) {
            return diagnosis;
        }
        throw new SecurityException("This result has not been released yet.");
    }

    public void setDiagnosis(User requester, String diagnosis) {
        if (requester.getRole() != Role.DOCTOR && requester.getRole() != Role.ADMIN) {
            throw new SecurityException("Only a doctor may finalize a diagnosis.");
        }
        this.diagnosis = diagnosis == null ? "" : diagnosis;
    }

    public boolean hasDiagnosis() {
        return diagnosis != null && !diagnosis.isBlank();
    }
}