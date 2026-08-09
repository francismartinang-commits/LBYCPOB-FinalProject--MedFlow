package com.dlsu.medflow.gui.labstaff;

import com.dlsu.medflow.gui.components.UI;
import com.dlsu.medflow.model.*;
import com.dlsu.medflow.service.HospitalDataStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class LabStaffDashboard extends VBox {

    private final LabStaff labStaff;
    private final HospitalDataStore store;
    private VBox queueContainer;

    public LabStaffDashboard(LabStaff labStaff, HospitalDataStore store) {
        this.labStaff = labStaff;
        this.store = store;
        setSpacing(20);
        setPadding(new Insets(28));
        build();
    }

    private void build() {
        Label title = UI.pageTitle(labStaff.getSection() + " Queue");
        Label subtitle = UI.pageSubtitle("Requests routed to your section, waiting to be encoded.");

        List<HospitalDataStore.LabQueueItem> pendingNow = store.getPendingLabRequests(labStaff.getSection());
        HBox stats = new HBox(16,
                UI.statCard(String.valueOf(pendingNow.size()), "Pending in Your Section"));

        queueContainer = new VBox(14);
        refreshQueue();

        getChildren().addAll(new VBox(4, title, subtitle), stats, UI.sectionTitle("Pending Requests"), queueContainer);
    }

    private void refreshQueue() {
        queueContainer.getChildren().clear();
        List<HospitalDataStore.LabQueueItem> items = store.getPendingLabRequests(labStaff.getSection());
        if (items.isEmpty()) {
            queueContainer.getChildren().add(UI.card(UI.body("No pending requests for " + labStaff.getSection() + " right now.")));
            return;
        }
        for (HospitalDataStore.LabQueueItem item : items) {
            queueContainer.getChildren().add(buildQueueCard(item));
        }
    }

    private VBox buildQueueCard(HospitalDataStore.LabQueueItem item) {
        Visit visit = item.getVisit();
        LabRequest request = item.getLabRequest();

        Label testName = UI.sectionTitle(request.getTestName());
        Label priority = UI.priorityBadge(request.getPriority());
        HBox topRow = new HBox(10, testName, (Node) UI.hSpacer(), priority);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label patientLine = UI.muted("Patient: " + visit.getPatient().getName() + "  ·  Visit " + visit.getVisitId());
        Label doctorLine = UI.muted("Ordered by " + (visit.getAssignedDoctor() != null ? visit.getAssignedDoctor().getName() : "-"));

        TextArea findingsArea = new TextArea();
        findingsArea.setPromptText("Enter laboratory findings...");
        findingsArea.setPrefRowCount(3);
        findingsArea.setWrapText(true);

        Label error = new Label();
        error.getStyleClass().add("login-error");
        error.setManaged(false);
        error.setVisible(false);

        Button submit = UI.primaryButton("Submit Findings");
        submit.setOnAction(e -> {
            String text = findingsArea.getText().trim();
            if (text.isEmpty()) {
                error.setText("Please enter findings before submitting.");
                error.setManaged(true);
                error.setVisible(true);
                return;
            }
            request.encodeFindings(text);
            if (visit.allFindingsEncoded()) {
                labStaff.updateStatus(visit, VisitStatus.FINDINGS_SENT_TO_DOCTOR);
            }
            store.save();
            refreshQueue();
        });

        return UI.card(topRow, patientLine, doctorLine, findingsArea, error, submit);
    }
}
