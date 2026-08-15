package com.dlsu.medflow.gui.components;

import com.dlsu.medflow.model.Visit;
import com.dlsu.medflow.model.VisitStatus;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class StatusTrackerView {

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM d, h:mm a");

    // UNDERSTAND:
    // JavaFX previously created VBox, HBox, Circle, and Label objects.
    // Spring Boot instead prepares the information that the web page displays.
    public List<StatusRow> buildTracker(Visit visit) {

        List<StatusRow> rows = new ArrayList<>();
        VisitStatus[] allStages = VisitStatus.values();
        int currentIndex = visit.getStatus().ordinal();

        for (int i = 0; i < allStages.length; i++) {

            VisitStatus stage = allStages[i];

            boolean completed = i < currentIndex;
            boolean current = i == currentIndex;
            boolean last = i == allStages.length - 1;

            rows.add(new StatusRow(
                    stage.getStageNumber(),
                    stage.getLabel(),
                    stage.getDescription(),
                    completed,
                    current,
                    last,
                    findTimestamp(visit, stage)
            ));
        }

        return rows;
    }

    // UNDERSTAND:
    // This keeps the same timestamp behavior from the JavaFX version.
    private String findTimestamp(Visit visit, VisitStatus stage) {

        for (Visit.StatusLogEntry entry : visit.getHistory()) {

            if (entry.getStatus() == stage) {
                return entry.getTimestamp().format(TIME_FORMAT);
            }
        }

        return null;
    }

    // DECISION:
    // Each StatusRow contains the information the Thymeleaf page
    // needs instead of storing JavaFX controls.
    public record StatusRow(
            int stageNumber,
            String label,
            String description,
            boolean completed,
            boolean current,
            boolean last,
            String timestamp
    ) {
    }
}