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

    private String findTimestamp(Visit visit, VisitStatus stage) {
        for (Visit.StatusLogEntry entry : visit.getHistory()) {
            if (entry.getStatus() == stage) {
                return entry.getTimestamp().format(TIME_FORMAT);
            }
        }
        return null;

        private HBox buildRow(VisitStatus stage, boolean completed, boolean current, boolean last, String timestamp) {
            Color dotColor;
            String dotText;
            if (completed) {
                dotColor = Color.web("#2E9E6B");
                dotText = "\u2713";
            } else if (current) {
                dotColor = Color.web("#106E8A");
                dotText = String.valueOf(stage.getStageNumber());
            } else {
                dotColor = Color.web("#D6DEE2");
                dotText = String.valueOf(stage.getStageNumber());
            }

            Circle circle = new Circle(11, dotColor);
            Label dotLabel = new Label(dotText);
            dotLabel.setStyle("-fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold;");
            if (!completed && !current) {
                dotLabel.setStyle("-fx-text-fill: #7C8C94; -fx-font-size: 10px; -fx-font-weight: bold;");
            }

            javafx.scene.layout.StackPane dot = new javafx.scene.layout.StackPane(circle, dotLabel);

            Line connector = new Line(0, 0, 0, 34);
            connector.setStroke(completed ? Color.web("#2E9E6B") : Color.web("#D6DEE2"));
            connector.setStrokeWidth(2);
            VBox connectorBox = new VBox(connector);
            connectorBox.setAlignment(Pos.TOP_CENTER);
            connectorBox.setMinWidth(22);
            connectorBox.setPrefWidth(22);

            VBox dotColumn = new VBox(dot, last ? new Region() : connectorBox);
            dotColumn.setAlignment(Pos.TOP_CENTER);
            dotColumn.setMinWidth(22);

            Label title = new Label(stage.getStageNumber() + ". " + stage.getLabel());
            title.getStyleClass().add(current || completed ? "tracker-step-title" : "tracker-step-title-pending");

            Label desc = new Label(stage.getDescription());
            desc.getStyleClass().add("tracker-step-desc");
            desc.setWrapText(true);

            VBox textColumn = new VBox(2, title, desc);
            if (timestamp != null) {
                Label time = new Label(timestamp);
                time.getStyleClass().add("tracker-step-time");
                textColumn.getChildren().add(time);
            }
            HBox.setHgrow(textColumn, Priority.ALWAYS);
            textColumn.setPadding(new Insets(0, 0, last ? 0 : 18, 0));


}