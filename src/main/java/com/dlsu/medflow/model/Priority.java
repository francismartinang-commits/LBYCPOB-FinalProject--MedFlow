package com.dlsu.medflow.model;

import java.io.Serializable;

/** Priority flag for a laboratory request, as mentioned under Polymorphism (createRequest overload). */
public enum Priority implements Serializable {
    ROUTINE("Routine"),
    URGENT("Urgent"),
    STAT("STAT");

    private final String label;

    Priority(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** CSS badge tone for this priority, mirrors {@link VisitStatus#getBadgeTone()}. */
    public String getBadgeTone() {
        switch (this) {
            case STAT:
                return "danger";
            case URGENT:
                return "warning";
            default:
                return "neutral";
        }
    }
}


