package com.dlsu.medflow.gui.components;

import com.dlsu.medflow.model.Priority;
import com.dlsu.medflow.model.VisitStatus;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Small factory of consistently-styled widgets so every dashboard shares the
 * same look and feel instead of repeating inline style strings everywhere.
 * Purely a GUI convenience class - it holds no application state.
 */
public final class UI {

    private UI() {
    }

    public static Label pageTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("page-title");
        return label;
    }

    public static Label pageSubtitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("page-subtitle");
        label.setWrapText(true);
        return label;
    }

    public static Label sectionTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
        return label;
    }

    public static Label muted(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("muted-text");
        return label;
    }

    public static Label body(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("body-text");
        label.setWrapText(true);
        return label;
    }

    public static VBox card(Node... children) {
        VBox box = new VBox(10, children);
        box.getStyleClass().add("card");
        box.setPadding(new Insets(18));
        return box;
    }

    public static VBox statCard(String value, String label) {
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-card-value");
        Label captionLabel = new Label(label);
        captionLabel.getStyleClass().add("stat-card-label");
        VBox box = new VBox(4, valueLabel, captionLabel);
        box.getStyleClass().add("stat-card");
        box.setPadding(new Insets(16));
        box.setMinWidth(150);
        return box;
    }

    public static Button primaryButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("btn-primary");
        return button;
    }

    public static Button secondaryButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("btn-secondary");
        return button;
    }

    public static Button successButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("btn-success");
        return button;
    }

    public static Button dangerButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("btn-danger");
        return button;
    }

    public static Button linkButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("btn-link");
        return button;
    }

    public static Label badge(String text, String tone) {
        Label label = new Label(text);
        label.getStyleClass().addAll("badge", "badge-" + tone);
        return label;
    }

    public static Label statusBadge(VisitStatus status) {
        String tone;
        switch (status) {
            case RELEASED_TO_PATIENT:
                tone = "success";
                break;
            case REGISTERED:
                tone = "neutral";
                break;
            case DOCTOR_REVIEWED:
            case FINDINGS_SENT_TO_DOCTOR:
                tone = "info";
                break;
            default:
                tone = "warning";
        }
        return badge(status.getLabel(), tone);
    }

    public static Label priorityBadge(Priority priority) {
        String tone;
        switch (priority) {
            case STAT:
                tone = "danger";
                break;
            case URGENT:
                tone = "warning";
                break;
            default:
                tone = "neutral";
        }
        return badge(priority.getLabel(), tone);
    }

    public static VBox fieldGroup(String labelText, Control control) {
        Label label = new Label(labelText);
        label.getStyleClass().add("field-label");
        if (control.getPrefWidth() < 1) {
            control.setPrefWidth(260);
        }
        VBox box = new VBox(4, label, control);
        return box;
    }

    public static HBox spacedRow(double spacing, Node... children) {
        HBox row = new HBox(spacing, children);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    public static Node hSpacer() {
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        return spacer;
    }
}
}