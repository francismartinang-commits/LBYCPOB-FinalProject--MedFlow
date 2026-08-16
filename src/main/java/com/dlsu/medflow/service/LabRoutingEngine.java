package com.dlsu.medflow.gui.patient;

import com.dlsu.medflow.gui.components.UI;
import com.dlsu.medflow.model.Patient;
import com.dlsu.medflow.model.Visit;
import com.dlsu.medflow.service.HospitalDataStore;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * "Patient registration" + "Reason-for-visit input" from the System
 * Framework, combined into a single walk-up self-registration form. On
 * success this creates both the {@link Patient} account and their first
 * {@link Visit}, which immediately receives a system Doctor Recommendation.
 */