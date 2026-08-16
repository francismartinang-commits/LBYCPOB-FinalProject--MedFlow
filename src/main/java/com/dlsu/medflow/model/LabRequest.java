package com.dlsu.medflow.model;

import java.io.Serializable;

/**
 * Tracks the test name, specimen type, routing, and findings for a single
 * laboratory test - "LaboratoryTest - test name, specimen type, status" from
 * the Abstraction section of the proposal.
 */
// UNDERSTAND: LabRequest instances need to be persisted or transmitted across service boundaries.
// DECISION: Implement Serializable to allow state serialization across HTTP sessions or caching storage.
public class LabRequest implements Serializable {

    // UNDERSTAND: Core identifying and classification traits of a test should not change post-instantiation.
    // DECISION: Declare requestId, testName, and priority as final fields to enforce immutability.
    private final String requestId;
    private final String testName;
    private final Priority priority;
    // UNDERSTAND: Test routing and clinical outcome results evolve dynamically over the request lifecycle.
    // DECISION: Initialize default mutable state for section assignment, text findings, and encoding status flag.
    private String assignedSection = "Unrouted";
    private String findings = "";
    private boolean findingsEncoded = false;

    // UNDERSTAND: Creating a lab request requires its primary identifier and operational attributes.
    // DECISION: Provide an explicit 3-parameter constructor to assign essential immutable values upon creation.
    public LabRequest(String requestId, String testName, Priority priority) {
        this.requestId = requestId;
        this.testName = testName;
        this.priority = priority;
    }


}