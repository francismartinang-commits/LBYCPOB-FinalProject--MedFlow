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
}