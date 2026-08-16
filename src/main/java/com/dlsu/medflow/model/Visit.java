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