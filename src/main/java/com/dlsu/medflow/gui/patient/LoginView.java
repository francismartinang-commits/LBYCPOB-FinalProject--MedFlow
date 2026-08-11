package com.dlsu.medflow.gui.patient;

import com.dlsu.medflow.gui.components.UI;
import com.dlsu.medflow.gui.patient.PatientRegistrationDialog;
import com.dlsu.medflow.model.User;
import com.dlsu.medflow.service.HospitalDataStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;

import java.util.function.Consumer;

/**
 * The application's entry screen. Split-panel design: a branded hero panel
 * on the left, and the sign-in form on the right - including a "quick fill"
 * demo-account panel so every role can be tried out immediately.
 */
public class LoginView extends BorderPane {

    public LoginView(HospitalDataStore store, Consumer<User> onLoginSuccess) {
        setLeft(buildHeroPanel());
        setCenter(buildFormPanel(store, onLoginSuccess));
        getStyleClass().add("root-bg");
    }

    private Node buildHeroPanel() {
        Label badge = new Label("DE LA SALLE UNIVERSITY  ·  LBYCPOB PROJECT");
        badge.getStyleClass().add("login-hero-badge");

        Label title = new Label("MedFlow");
        title.getStyleClass().add("login-hero-title");

        Label subtitle = new Label(
                "Hospital & Laboratory Information System\n\n"
                        + "Follow every patient from registration to released results - with role-based "
                        + "access for Patients, Doctors, Nurses/Staff, Laboratory Staff, and Admins.");
        subtitle.getStyleClass().add("login-hero-subtitle");
        subtitle.setMaxWidth(320);

        Polyline pulse = buildPulseIcon();

        VBox content = new VBox(18, badge, pulse, title, subtitle);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(50));
        content.setMaxWidth(420);

        StackPane hero = new StackPane(content);
        hero.getStyleClass().add("login-hero");
        hero.setPrefWidth(440);
        StackPane.setAlignment(content, Pos.CENTER_LEFT);
        return hero;
    }

    private Polyline buildPulseIcon() {
        Polyline line = new Polyline(
                0, 20,
                40, 20,
                55, 0,
                70, 40,
                85, 20,
                100, 20,
                115, 5,
                130, 35,
                145, 20,
                200, 20
        );
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(3);
        line.setFill(null);
        return line;
    }

    private Node buildFormPanel(HospitalDataStore store, Consumer<User> onLoginSuccess) {
        Label welcome = new Label("Welcome back");
        welcome.getStyleClass().add("login-form-title");

        Label instructions = UI.muted("Sign in with your MedFlow account to continue.");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label error = new Label();
        error.getStyleClass().add("login-error");
        error.setManaged(false);
        error.setVisible(false);

        Button loginButton = UI.primaryButton("Log In");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setDefaultButton(true);

        Runnable attemptLogin = () -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            if (username.isEmpty() || password.isEmpty()) {
                error.setText("Please enter both your username and password.");
                error.setManaged(true);
                error.setVisible(true);
                return;
            }
            User user = store.authenticate(username, password);
            if (user == null) {
                error.setText("Incorrect username or password. Please try again.");
                error.setManaged(true);
                error.setVisible(true);
                return;
            }
            onLoginSuccess.accept(user);
        };
        loginButton.setOnAction(e -> attemptLogin.run());

        Button registerLink = UI.linkButton("New patient? Register here");
        registerLink.setOnAction(e -> {
            PatientRegistrationDialog dialog = new PatientRegistrationDialog(store);
            dialog.showAndWait().ifPresent(username -> {
                usernameField.setText(username);
                error.setText("Registration successful! Enter your password to log in.");
                error.getStyleClass().remove("login-error");
                error.setStyle("-fx-text-fill: #1E7A4C;");
                error.setManaged(true);
                error.setVisible(true);
            });
        });