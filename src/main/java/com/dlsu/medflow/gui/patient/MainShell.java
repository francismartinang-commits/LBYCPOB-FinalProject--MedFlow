package com.dlsu.medflow.gui;

import com.dlsu.medflow.gui.components.UI;
import com.dlsu.medflow.model.User;
import com.dlsu.medflow.service.HospitalDataStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * The shell every signed-in user sees: a slim identity/branding sidebar
 * that never changes, wrapped around whichever dashboard
 * {@link User#displayDashboard(HospitalDataStore)} polymorphically returns
 * for the current user's role.
 */
public class MainShell extends BorderPane {

    public MainShell(User user, HospitalDataStore store, Runnable onLogout) {
        setLeft(buildSidebar(user, onLogout));

        Node dashboard = user.displayDashboard(store);
        ScrollPane scrollPane = new ScrollPane(dashboard);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("edge-to-edge");
        setCenter(scrollPane);

        getStyleClass().add("root-bg");
    }

    private Node buildSidebar(User user, Runnable onLogout) {
        Label brand = new Label("MedFlow");
        brand.getStyleClass().add("sidebar-brand");

        Circle avatar = new Circle(26, Color.web("#1FA6A0"));
        Label initials = new Label(initialsOf(user.getName()));
        initials.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        StackPane avatarStack = new StackPane(avatar, initials);

        Label nameLabel = new Label(user.getName());
        nameLabel.getStyleClass().add("sidebar-user-name");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(180);
