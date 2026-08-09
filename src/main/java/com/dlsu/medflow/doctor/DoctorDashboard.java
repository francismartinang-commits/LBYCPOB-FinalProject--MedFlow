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

    public DoctorDashboard(Doctor doctor, HospitalDataStore store) {
        this.doctor = doctor;
        this.store = store;
        setSpacing(20);
        setPadding(new Insets(28));
        build();
    }

    private void build() {
        Label title = UI.pageTitle("Dr. " + lastNameOnly(doctor.getName()) + "'s Dashboard");
        Label subtitle = UI.pageSubtitle(doctor.getSpecialization() + "  ·  Assigned patients, assessments, and laboratory requests.");
        VBox headerText = new VBox(4, title, subtitle);

        List<Visit> visits = store.getVisitsForDoctor(doctor);
        long awaitingAssessment = visits.stream().filter(v -> v.getStatus() == VisitStatus.ASSIGNED_TO_DOCTOR).count();
        long awaitingFindings = visits.stream().filter(v -> v.getStatus() == VisitStatus.FINDINGS_SENT_TO_DOCTOR).count();
        long inProgress = visits.stream().filter(v -> v.getStatus() != VisitStatus.RELEASED_TO_PATIENT).count();
        long completed = visits.stream().filter(v -> v.getStatus() == VisitStatus.RELEASED_TO_PATIENT).count();

        HBox stats = new HBox(16,
                UI.statCard(String.valueOf(inProgress), "Active Patients"),
                UI.statCard(String.valueOf(awaitingAssessment), "Awaiting Assessment"),
                UI.statCard(String.valueOf(awaitingFindings), "Findings To Review"),
                UI.statCard(String.valueOf(completed), "Completed"));

        visitListContainer = new VBox(16);
        refreshVisitList();

        getChildren().addAll(headerText, stats, UI.sectionTitle("Assigned Patients"), visitListContainer);
    }
}