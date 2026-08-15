package com.dlsu.medflow.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

import java.io.Serializable;

/**
 * Tracks the test name, specimen type, routing, and findings for a single
 * laboratory test - "LaboratoryTest - test name, specimen type, status" from
 * the Abstraction section of the proposal.
 */
@Entity
public class LabRequest implements Serializable {

    @Id
    private String requestId;

    private String testName;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private String assignedSection = "Unrouted";
    private String findings = "";
    private boolean findingsEncoded = false;

    // UNDERSTAND:
    // JPA requires a no-argument constructor when loading
    // laboratory requests from the database.
    protected LabRequest() {
    }

    public LabRequest(String requestId, String testName, Priority priority) {
        this.requestId = requestId;
        this.testName = testName;
        this.priority = priority;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getTestName() {
        return testName;
    }

    public Priority getPriority() {
        return priority;
    }

    public String getAssignedSection() {
        return assignedSection;
    }

    public void setAssignedSection(String assignedSection) {
        this.assignedSection = assignedSection;
    }

    public String getFindings() {
        return findings;
    }

    public boolean isFindingsEncoded() {
        return findingsEncoded;
    }

    /** Called only by Laboratory Staff dashboards after they encode a result. */
    public void encodeFindings(String findings) {
        if (findings == null || findings.isBlank()) {
            throw new IllegalArgumentException("Findings cannot be empty.");
        }
        this.findings = findings;
        this.findingsEncoded = true;
    }

    @Override
    public String toString() {
        return testName + " [" + priority.getLabel() + "] -> " + assignedSection;
    }
}