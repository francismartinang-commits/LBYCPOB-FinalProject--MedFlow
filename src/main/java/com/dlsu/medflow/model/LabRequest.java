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
}