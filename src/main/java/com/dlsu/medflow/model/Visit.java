package com.dlsu.medflow.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single patient visit as it moves through
 * the hospital workflow.
 */
@Entity
public class Visit implements Serializable {

    @Id
    private String visitId;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    private String reasonForVisit;
    private LocalDateTime registeredAt;

    @Enumerated(EnumType.STRING)
    private VisitStatus status = VisitStatus.REGISTERED;

    @ManyToOne
    @JoinColumn(name = "recommended_doctor_id")
    private Doctor recommendedDoctor;

    @ManyToOne
    @JoinColumn(name = "assigned_doctor_id")
    private Doctor assignedDoctor;

    @Embedded
    private MedicalRecord medicalRecord = new MedicalRecord();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "visit_id")
    private List<LabRequest> labRequests = new ArrayList<>();

    // UNDERSTAND:
    // JPA requires a no-argument constructor when loading visits.
    protected Visit() {
    }

    public Visit(
            String visitId,
            Patient patient,
            String reasonForVisit) {

        this.visitId = visitId;
        this.patient = patient;
        this.reasonForVisit = reasonForVisit;
        this.registeredAt = LocalDateTime.now();
    }

    // UNDERSTAND:
    // Visit status can only be changed through a validated User action.
    public void advance(User approver, VisitStatus newStatus) {
        advance(approver, newStatus, LocalDateTime.now());
    }

    public void advance(
            User approver,
            VisitStatus newStatus,
            LocalDateTime when) {

        if (approver == null) {
            throw new SecurityException(
                    "A visit's status can only change through a validated user action.");
        }

        this.status = newStatus;
    }

    /** True once every laboratory request has encoded findings. */
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

    public void addLabRequest(LabRequest request) {
        labRequests.add(request);
    }

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

    @Override
    public String toString() {
        return "Visit " + visitId + " - "
                + patient.getName()
                + " (" + status.getLabel() + ")";
    }
}