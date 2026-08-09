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

    private Node buildActionSection(Visit visit, Stage dialog) {
        VBox section = new VBox(14);

        boolean hasNotes = !visit.getMedicalRecord().getDoctorNotes(doctor).isBlank();
        if (hasNotes) {
            section.getChildren().add(UI.card(
                    UI.sectionTitle("Clinical Notes"),
                    UI.body(visit.getMedicalRecord().getDoctorNotes(doctor))));
        }

        if (!visit.getLabRequests().isEmpty()) {
            VBox labBox = new VBox(6);
            labBox.getChildren().add(UI.sectionTitle("Laboratory Requests"));
            for (LabRequest request : visit.getLabRequests()) {
                HBox row = new HBox(10,
                        UI.body(request.getTestName()),
                        UI.priorityBadge(request.getPriority()),
                        UI.muted("-> " + request.getAssignedSection()));
                row.setAlignment(Pos.CENTER_LEFT);
                labBox.getChildren().add(row);
                if (request.isFindingsEncoded()) {
                    Label findings = UI.body("Findings: " + request.getFindings());
                    labBox.getChildren().add(findings);
                }
            }
            javafx.scene.Node[] labNodes = labBox.getChildren().toArray(new javafx.scene.Node[0]);
            // relocate into its own card without double-parenting
            labBox.getChildren().clear();
            section.getChildren().add(UI.card(labNodes));
        }

        switch (visit.getStatus()) {
            case ASSIGNED_TO_DOCTOR:
                section.getChildren().add(buildBeginAssessmentBox(visit));
                break;
            case UNDER_DOCTOR_ASSESSMENT:
                section.getChildren().add(buildAssessmentBox(visit));
                break;
            case FINDINGS_SENT_TO_DOCTOR:
                section.getChildren().add(buildReviewBox(visit));
                break;
            case DOCTOR_REVIEWED:
                section.getChildren().add(buildReleaseBox(visit));
                break;
            default:
                // LABORATORY_REQUESTED / SAMPLE_COLLECTED / SENT_TO_LABORATORY / UNDER_LABORATORY_ANALYSIS /
                // RELEASED_TO_PATIENT: nothing further for the doctor to action right now.
                break;
        }

        return section;
    }

    private Node buildBeginAssessmentBox(Visit visit) {
        Button begin = UI.primaryButton("Begin Assessment");
        begin.setOnAction(e -> {
            doctor.updateStatus(visit, VisitStatus.UNDER_DOCTOR_ASSESSMENT);
            store.save();
            begin.setDisable(true);
            begin.setText("Assessment Started - reopen to continue");
        });
        return UI.card(UI.sectionTitle("Ready to see this patient?"), begin);
    }

    private Node buildAssessmentBox(Visit visit) {
        TextArea notesArea = new TextArea(visit.getMedicalRecord().getDoctorNotes(doctor));
        notesArea.setPromptText("Clinical assessment notes...");
        notesArea.setPrefRowCount(3);
        notesArea.setWrapText(true);

        Button saveNotes = UI.secondaryButton("Save Notes");
        Label savedTag = UI.muted("");
        saveNotes.setOnAction(e -> {
            visit.getMedicalRecord().setDoctorNotes(doctor, notesArea.getText().trim());
            store.save();
            savedTag.setText("Saved.");
        });

        TextField singleTestField = new TextField();
        singleTestField.setPromptText("e.g., CBC (Complete Blood Count)");
        Button addSingle = UI.secondaryButton("Add Single Test");
        Label singleStatus = UI.muted("");
        addSingle.setOnAction(e -> {
            String testName = singleTestField.getText().trim();
            if (testName.isEmpty()) {
                singleStatus.setText("Enter a test name first.");
                return;
            }
            doctor.createRequest(visit, testName);
            store.save();
            singleStatus.setText("Added and routed automatically.");
            singleTestField.clear();
        });

        TextField batchTestsField = new TextField();
        batchTestsField.setPromptText("e.g., CBC, Urinalysis, X-ray Chest");
        ComboBox<Priority> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll(Priority.values());
        priorityBox.getSelectionModel().select(Priority.ROUTINE);
        Button addBatch = UI.secondaryButton("Add Batch");
        Label batchStatus = UI.muted("");
        addBatch.setOnAction(e -> {
            String raw = batchTestsField.getText().trim();
            if (raw.isEmpty()) {
                batchStatus.setText("Enter at least one test name.");
                return;
            }
            String[] testNames = raw.split(",");
            doctor.createRequest(visit, testNames, priorityBox.getValue().name());
            store.save();
            batchStatus.setText("Batch added and routed automatically.");
            batchTestsField.clear();
        });

        VBox singleRow = new VBox(6, UI.fieldGroup("Single Test (createRequest(String))", singleTestField),
                new HBox(10, addSingle, singleStatus));
        VBox batchRow = new VBox(6,
                UI.fieldGroup("Batch Tests, comma-separated (createRequest(String[], String))", batchTestsField),
                new HBox(10, priorityBox, addBatch, batchStatus));

        return UI.card(
                UI.sectionTitle("Assessment"),
                notesArea,
                new HBox(10, saveNotes, savedTag),
                new Separator(),
                UI.sectionTitle("Create Laboratory Request"),
                singleRow,
                batchRow);
    }

    private Node buildReviewBox(Visit visit) {
        TextArea diagnosisArea = new TextArea();
        diagnosisArea.setPromptText("Final diagnosis and advice for the patient...");
        diagnosisArea.setPrefRowCount(3);
        diagnosisArea.setWrapText(true);

        Label error = new Label();
        error.getStyleClass().add("login-error");
        error.setManaged(false);
        error.setVisible(false);

        Button confirm = UI.primaryButton("Confirm Review");
        confirm.setOnAction(e -> {
            String diagnosisText = diagnosisArea.getText().trim();
            if (diagnosisText.isEmpty()) {
                error.setText("Please write a diagnosis before confirming.");
                error.setManaged(true);
                error.setVisible(true);
                return;
            }
            visit.getMedicalRecord().setDiagnosis(doctor, diagnosisText);
            doctor.updateStatus(visit, VisitStatus.DOCTOR_REVIEWED);
            store.save();
            confirm.setDisable(true);
            confirm.setText("Reviewed - reopen to release");
        });

        return UI.card(UI.sectionTitle("Review Laboratory Findings"), diagnosisArea, error, confirm);
    }

    private Node buildReleaseBox(Visit visit) {
        Button release = UI.successButton("Release Results to Patient");
        release.setOnAction(e -> {
            doctor.updateStatus(visit, VisitStatus.RELEASED_TO_PATIENT);
            store.save();
            release.setDisable(true);
            release.setText("Released");
        });
        return UI.card(UI.sectionTitle("Ready to release?"),
                UI.body("The patient will immediately be able to see the diagnosis and laboratory findings."),
                release);
    }
}
