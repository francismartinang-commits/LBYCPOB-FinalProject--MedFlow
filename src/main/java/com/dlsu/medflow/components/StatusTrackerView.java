package com.dlsu.medflow.gui.components;

import com.dlsu.medflow.model.Visit;
import com.dlsu.medflow.model.VisitStatus;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class StatusTrackerView extends VBox {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("MMM d, h:mm a");

    public StatusTrackerView(Visit visit) {
        setSpacing(0);
        VisitStatus[] allStages = VisitStatus.values();
        int currentIndex = visit.getStatus().ordinal();

        for (int i = 0; i < allStages.length; i++) {
            VisitStatus stage = allStages[i];
            boolean completed = i < currentIndex;
            boolean current = i == currentIndex;
            boolean last = i == allStages.length - 1;

            getChildren().add(buildRow(stage, completed, current, last, findTimestamp(visit, stage)));
        }
    }
}