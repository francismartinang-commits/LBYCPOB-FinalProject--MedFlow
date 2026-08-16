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