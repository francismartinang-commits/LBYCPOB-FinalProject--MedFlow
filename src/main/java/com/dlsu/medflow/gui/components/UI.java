package com.dlsu.medflow.gui.components;

import com.dlsu.medflow.model.Priority;
import com.dlsu.medflow.model.VisitStatus;

/**
 * Provides reusable presentation values for the Spring Boot web interface.
 *
 * UNDERSTAND:
 * The old JavaFX version created Labels, Buttons, VBox, and HBox objects.
 * Thymeleaf and CSS now handle those visual elements in the web version.
 */
public final class UI {

    private UI() {
    }

    // UNDERSTAND:
    // Returns the CSS tone used when displaying a VisitStatus badge.
    public static String statusTone(VisitStatus status) {

        return switch (status) {
            case RELEASED_TO_PATIENT -> "success";
            case REGISTERED -> "neutral";

            case DOCTOR_REVIEWED,
                 FINDINGS_SENT_TO_DOCTOR -> "info";

            default -> "warning";
        };
    }

    // UNDERSTAND:
    // Returns the CSS tone used when displaying a laboratory priority.
    public static String priorityTone(Priority priority) {

        return switch (priority) {
            case STAT -> "danger";
            case URGENT -> "warning";
            default -> "neutral";
        };
    }

    // DECISION:
    // Instead of creating a JavaFX Label, the web version returns
    // the CSS class that Thymeleaf can place on an HTML element.
    public static String statusBadgeClass(VisitStatus status) {
        return "badge badge-" + statusTone(status);
    }

    public static String priorityBadgeClass(Priority priority) {
        return "badge badge-" + priorityTone(priority);
    }
}