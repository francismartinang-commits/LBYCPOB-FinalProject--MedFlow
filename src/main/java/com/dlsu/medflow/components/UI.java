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
}