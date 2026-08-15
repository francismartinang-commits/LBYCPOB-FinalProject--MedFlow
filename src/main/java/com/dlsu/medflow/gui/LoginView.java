package com.dlsu.medflow.gui;

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

        VBox formFields = new VBox(12,
                UI.fieldGroup("Username", usernameField),
                UI.fieldGroup("Password", passwordField),
                error,
                loginButton,
                registerLink);

        VBox formCard = UI.card(welcome, instructions, new Separator(), formFields);
        formCard.setMaxWidth(360);
        formCard.setPrefWidth(360);

        TitledPane demoPane = buildDemoAccountsPane(usernameField, passwordField, error);
        demoPane.setMaxWidth(360);

        VBox centerStack = new VBox(20, formCard, demoPane);
        centerStack.setAlignment(Pos.CENTER);
        centerStack.setPadding(new Insets(50));

        StackPane wrapper = new StackPane(centerStack);
        StackPane.setAlignment(centerStack, Pos.CENTER);
        return wrapper;
    }

    private TitledPane buildDemoAccountsPane(TextField usernameField, PasswordField passwordField, Label error) {
        VBox rows = new VBox(6);
        rows.setPadding(new Insets(8));
        rows.getStyleClass().add("demo-creds-box");

        String[][] demos = {
                {"Admin", "admin", "admin123"},
                {"Doctor - Dr. Ana Reyes (General Medicine)", "dr.reyes", "doctor123"},
                {"Doctor - Dr. Miguel Santos (Cardiology)", "dr.santos", "doctor123"},
                {"Nurse / Staff - Liza Ramos", "nurse.ramos", "nurse123"},
                {"Laboratory Staff - Jun Dizon (Hematology)", "lab.dizon", "lab123"},
                {"Laboratory Staff - Ella Manalo (Chemistry)", "lab.manalo", "lab123"},
                {"Patient - Juan Dela Cruz", "patient.juan", "patient123"},
        };

        for (String[] demo : demos) {
            Label roleLabel = UI.muted(demo[0]);
            Label credsLabel = new Label(demo[1] + " / " + demo[2]);
            credsLabel.setStyle("-fx-font-size: 11.5px; -fx-font-family: monospace;");
            Button fill = UI.linkButton("Use");
            fill.setOnAction(e -> {
                usernameField.setText(demo[1]);
                passwordField.setText(demo[2]);
                error.setManaged(false);
                error.setVisible(false);
            });
            HBox row = new HBox(8, credsLabel, UI.hSpacer(), fill);
            row.setAlignment(Pos.CENTER_LEFT);
            VBox pair = new VBox(1, roleLabel, row);
            rows.getChildren().add(pair);
        }

        TitledPane pane = new TitledPane("Demo accounts (for quick testing)", rows);
        pane.setExpanded(false);
        return pane;
    }
}