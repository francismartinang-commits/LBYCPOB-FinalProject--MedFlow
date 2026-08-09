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

    private void refreshVisitList() {
        visitListContainer.getChildren().clear();
        List<Visit> visits = store.getVisitsForDoctor(doctor);
        if (visits.isEmpty()) {
            visitListContainer.getChildren().add(UI.card(UI.body("No patients have been assigned to you yet.")));
            return;
        }
        visits.sort((a, b) -> Boolean.compare(
                a.getStatus() == VisitStatus.RELEASED_TO_PATIENT,
                b.getStatus() == VisitStatus.RELEASED_TO_PATIENT));

        for (Visit visit : visits) {
            visitListContainer.getChildren().add(buildVisitCard(visit));
        }
    }

    private VBox buildVisitCard(Visit visit) {
        Label patientName = UI.sectionTitle(visit.getPatient().getName());
        Label reason = UI.muted(visit.getReasonForVisit());
        Label statusBadge = UI.statusBadge(visit.getStatus());

        Button openButton = UI.primaryButton(actionLabelFor(visit.getStatus()));
        openButton.setOnAction(e -> openWorkspace(visit));

        HBox topRow = new HBox(12, patientName, (Node) UI.hSpacer(), statusBadge);
        topRow.setAlignment(Pos.CENTER_LEFT);

        return UI.card(topRow, reason, openButton);
    }

    private String actionLabelFor(VisitStatus status) {
        switch (status) {
            case ASSIGNED_TO_DOCTOR:
                return "Begin Assessment";
            case FINDINGS_SENT_TO_DOCTOR:
                return "Review Findings";
            case DOCTOR_REVIEWED:
                return "Release to Patient";
            case RELEASED_TO_PATIENT:
                return "View Record";
            default:
                return "Open";
        }
    }

    private String lastNameOnly(String fullName) {
        String withoutTitle = fullName.replace("Dr. ", "");
        String[] parts = withoutTitle.split(" ");
        return parts[parts.length - 1];
    }

    // -------------------------------------------------------------------
    // Visit workspace dialog
    // -------------------------------------------------------------------

    private void openWorkspace(Visit visit) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(visit.getPatient().getName() + " - " + visit.getVisitId());

        VBox content = new VBox(18);
        content.setPadding(new Insets(24));

        content.getChildren().add(UI.pageTitle(visit.getPatient().getName()));
        content.getChildren().add(UI.muted("Reason for visit: " + visit.getReasonForVisit()));
        content.getChildren().add(UI.muted("Patient contact: " + visit.getPatient().getContactNumber()
                + "  ·  Age " + visit.getPatient().getAge() + "  ·  " + visit.getPatient().getGender()));

        content.getChildren().add(buildActionSection(visit, dialog));
        content.getChildren().add(UI.sectionTitle("Status Timeline"));
        content.getChildren().add(new StatusTrackerView(visit));

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("edge-to-edge");

        javafx.scene.layout.BorderPane root = new javafx.scene.layout.BorderPane(scrollPane);
        root.getStyleClass().add("root-bg");

        Scene scene = new Scene(root, 620, 720);
        scene.getStylesheets().add(getClass().getResource("/com/dlsu.medflow/styles.css").toExternalForm());
        dialog.setScene(scene);
        dialog.showAndWait();
        refreshVisitList();
    }
}