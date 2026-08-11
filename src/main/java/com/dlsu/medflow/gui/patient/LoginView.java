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
