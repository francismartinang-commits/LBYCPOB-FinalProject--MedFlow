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
}