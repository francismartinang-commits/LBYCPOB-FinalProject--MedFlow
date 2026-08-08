package com.dlsu.medflow.gui.admin;

import com.dlsu.medflow.gui.components.UI;
import com.dlsu.medflow.model.Admin;
import com.dlsu.medflow.service.HospitalDataStore;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AdminDashboard extends VBox {

    private final Admin admin;
    private final HospitalDataStore store;

    public AdminDashboard(Admin admin, HospitalDataStore store) {
        this.admin = admin;
        this.store = store;

        setSpacing(20);
        setPadding(new Insets(28));

        build();
    }

    private void build() {
        Label title =
                UI.pageTitle("Admin Dashboard");

        Label subtitle =
                UI.pageSubtitle(
                        "Manage accounts, doctor categories, and laboratory sections."
                );

        getChildren().add(
                new VBox(
                        4,
                        title,
                        subtitle
                )
        );
    }
}