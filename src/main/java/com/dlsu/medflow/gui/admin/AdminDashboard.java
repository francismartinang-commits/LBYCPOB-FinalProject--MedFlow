package com.dlsu.medflow.gui.admin;

import com.dlsu.medflow.gui.components.UI;
import com.dlsu.medflow.model.*;
import com.dlsu.medflow.service.HospitalDataStore;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class AdminDashboard extends VBox {

    private final Admin admin;
    private final HospitalDataStore store;
    private TableView<User> accountsTable;
    private VBox overviewStats;
    private ListView<String> categoriesList;
    private ListView<String> sectionsList;

    public AdminDashboard(Admin admin, HospitalDataStore store) {
        this.admin = admin;
        this.store = store;
        setSpacing(20);
        setPadding(new Insets(28));
        build();
    }

    private void build() {
        Label title = UI.pageTitle("Admin Dashboard");
        Label subtitle = UI.pageSubtitle("Manage accounts, doctor categories, and laboratory sections.");

        Tab overviewTab = new Tab("Overview", buildOverviewTab());
        Tab accountsTab = new Tab("Manage Accounts", buildAccountsTab());
        Tab categoriesTab = new Tab("Doctor Categories", buildCategoriesTab());
        Tab sectionsTab = new Tab("Laboratory Sections", buildSectionsTab());
        for (Tab t : List.of(overviewTab, accountsTab, categoriesTab, sectionsTab)) {
            t.setClosable(false);
        }

        TabPane tabPane = new TabPane(overviewTab, accountsTab, categoriesTab, sectionsTab);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        getChildren().addAll(new VBox(4, title, subtitle), tabPane);
    }

    // -------------------------------------------------------------------
    // Overview
    // -------------------------------------------------------------------

    private Node buildOverviewTab() {
        overviewStats = new VBox(16);
        refreshOverview();
        VBox wrapper = new VBox(16, overviewStats);
        wrapper.setPadding(new Insets(16, 0, 0, 0));
        return wrapper;
    }

        accountsTab.setClosable(false);
        categoriesTab.setClosable(false);
        sectionsTab.setClosable(false);

        TabPane tabPane =
                new TabPane(
                        overviewTab,
                        accountsTab,
                        categoriesTab,
                        sectionsTab
                );

        tabPane.setTabClosingPolicy(
                TabPane.TabClosingPolicy.UNAVAILABLE
        );

        VBox.setVgrow(
                tabPane,
                Priority.ALWAYS
        );

        getChildren().addAll(
                new VBox(
                        4,
                        title,
                        subtitle
                ),
                tabPane
        );
    }

    private Node buildOverviewTab() {
        overviewStats =
                new VBox(16);

        refreshOverview();

        VBox wrapper =
                new VBox(
                        16,
                        overviewStats
                );

        wrapper.setPadding(
                new Insets(
                        16,
                        0,
                        0,
                        0
                )
        );

        return wrapper;
    }

    private void refreshOverview() {
        overviewStats
                .getChildren()
                .clear();

        long patients =
                store.getAllUsers()
                        .stream()
                        .filter(
                                u -> u.getRole()
                                        == Role.PATIENT
                        )
                        .count();

        long doctors =
                store.getAllUsers()
                        .stream()
                        .filter(
                                u -> u.getRole()
                                        == Role.DOCTOR
                        )
                        .count();

        long staff =
                store.getAllUsers()
                        .stream()
                        .filter(
                                u -> u.getRole()
                                        == Role.NURSE_STAFF
                                        || u.getRole()
                                        == Role.LAB_STAFF
                        )
                        .count();

        long activeVisits =
                store.getAllVisits()
                        .stream()
                        .filter(
                                v -> v.getStatus()
                                        != VisitStatus.RELEASED_TO_PATIENT
                        )
                        .count();

        HBox statsRow =
                new HBox(
                        16,
                        UI.statCard(
                                String.valueOf(patients),
                                "Registered Patients"
                        ),
                        UI.statCard(
                                String.valueOf(doctors),
                                "Doctors"
                        ),
                        UI.statCard(
                                String.valueOf(staff),
                                "Nurses & Lab Staff"
                        ),
                        UI.statCard(
                                String.valueOf(activeVisits),
                                "Active Visits"
                        )
                );

        List<Node> breakdownNodes =
                new java.util.ArrayList<>();

        breakdownNodes.add(
                UI.sectionTitle(
                        "Visits by Stage"
                )
        );

        for (VisitStatus status :
                VisitStatus.values()) {

            long count =
                    store.getAllVisits()
                            .stream()
                            .filter(
                                    v -> v.getStatus()
                                            == status
                            )
                            .count();

            HBox row =
                    new HBox(
                            10,
                            UI.body(
                                    status.getStageNumber()
                                            + ". "
                                            + status.getLabel()
                            ),
                            (Node) UI.hSpacer(),
                            UI.muted(
                                    String.valueOf(count)
                            )
                    );

            row.setAlignment(
                    Pos.CENTER_LEFT
            );

            breakdownNodes.add(row);
        }

        overviewStats
                .getChildren()
                .addAll(
                        statsRow,
                        UI.card(
                                breakdownNodes.toArray(
                                        new Node[0]
                                )
                        )
                );
    }

    @SuppressWarnings("unchecked")
    private Node buildAccountsTab() {
        accountsTable =
                new TableView<>();

        accountsTable.setPlaceholder(
                new Label(
                        "No accounts yet."
                )
        );

        TableColumn<User, String> nameCol =
                new TableColumn<>("Name");

        nameCol.setCellValueFactory(
                cd ->
                        new SimpleStringProperty(
                                cd.getValue()
                                        .getName()
                        )
        );

        TableColumn<User, String> roleCol =
                new TableColumn<>("Role");

        roleCol.setCellValueFactory(
                cd ->
                        new SimpleStringProperty(
                                roleDetail(
                                        cd.getValue()
                                )
                        )
        );

        TableColumn<User, String> usernameCol =
                new TableColumn<>(
                        "Username"
                );

        usernameCol.setCellValueFactory(
                cd ->
                        new SimpleStringProperty(
                                cd.getValue()
                                        .getUsername()
                        )
        );

        accountsTable
                .getColumns()
                .addAll(
                        nameCol,
                        roleCol,
                        usernameCol
                );

        accountsTable.setItems(
                FXCollections.observableArrayList(
                        store.getAllUsers()
                )
        );

        accountsTable
                .setColumnResizePolicy(
                        TableView.CONSTRAINED_RESIZE_POLICY
                );

        VBox.setVgrow(
                accountsTable,
                Priority.ALWAYS
        );

        VBox wrapper =
                new VBox(
                        12,
                        accountsTable
                );

        wrapper.setPadding(
                new Insets(
                        16,
                        0,
                        0,
                        0
                )
        );

        return wrapper;
    }

    private String roleDetail(
            User user) {

        if (user instanceof Doctor) {
            return "Doctor - "
                    + ((Doctor) user)
                    .getSpecialization();
        }

        if (user instanceof LabStaff) {
            return "Lab Staff - "
                    + ((LabStaff) user)
                    .getSection();
        }

        return user
                .getRole()
                .getDisplayName();
    }
}

void main() {
}