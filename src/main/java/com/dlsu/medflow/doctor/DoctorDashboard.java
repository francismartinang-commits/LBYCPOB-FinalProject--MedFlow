package com.dlsu.medflow.gui.doctor;

import com.dlsu.medflow.gui.components.StatusTrackerView;
import com.dlsu.medflow.gui.components.UI;
import com.dlsu.medflow.model.*;
import com.dlsu.medflow.service.HospitalDataStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

/**
 * POLYMORPHISM (method overriding): returned by {@code Doctor} from
 * {@link User#displayDashboard(HospitalDataStore)}. "Doctor - can view
 * assigned patients, add notes, and request lab tests" (System Framework).
 */
public class DoctorDashboard extends VBox {

    private final Doctor doctor;
    private final HospitalDataStore store;
    private VBox visitListContainer;
}