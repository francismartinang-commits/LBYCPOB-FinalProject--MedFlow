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

    private void refreshOverview() {
        overviewStats.getChildren().clear();

        long patients = store.getAllUsers().stream().filter(u -> u.getRole() == Role.PATIENT).count();
        long doctors = store.getAllUsers().stream().filter(u -> u.getRole() == Role.DOCTOR).count();
        long staff = store.getAllUsers().stream()
                .filter(u -> u.getRole() == Role.NURSE_STAFF || u.getRole() == Role.LAB_STAFF).count();
        long activeVisits = store.getAllVisits().stream()
                .filter(v -> v.getStatus() != VisitStatus.RELEASED_TO_PATIENT).count();

        HBox statsRow = new HBox(16,
                UI.statCard(String.valueOf(patients), "Registered Patients"),
                UI.statCard(String.valueOf(doctors), "Doctors"),
                UI.statCard(String.valueOf(staff), "Nurses & Lab Staff"),
                UI.statCard(String.valueOf(activeVisits), "Active Visits"));

        List<Node> breakdownNodes = new java.util.ArrayList<>();
        breakdownNodes.add(UI.sectionTitle("Visits by Stage"));
        for (VisitStatus status : VisitStatus.values()) {
            long count = store.getAllVisits().stream().filter(v -> v.getStatus() == status).count();
            HBox row = new HBox(10, UI.body(status.getStageNumber() + ". " + status.getLabel()),
                    (Node) UI.hSpacer(), UI.muted(String.valueOf(count)));
            row.setAlignment(Pos.CENTER_LEFT);
            breakdownNodes.add(row);
        }


        overviewStats.getChildren().addAll(statsRow, UI.card(breakdownNodes.toArray(new Node[0])));
    }

    // -------------------------------------------------------------------
    // Accounts
    // -------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private Node buildAccountsTab() {
        accountsTable = new TableView<>();
        accountsTable.setPlaceholder(new Label("No accounts yet."));

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cd -> new SimpleStringProperty(roleDetail(cd.getValue())));

        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getUsername()));

        TableColumn<User, Void> statusCol = new TableColumn<>("Status");
        statusCol.setCellFactory(col -> new TableCell<User, Void>() {
            private final Button toggle = new Button();

            {
                toggle.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    if (user == admin) {
                        return;
                    }
                    user.setActive(!user.isActive());
                    store.save();
                    getTableView().refresh();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                User user = getTableView().getItems().get(getIndex());
                toggle.setText(user.isActive() ? "Deactivate" : "Activate");
                toggle.getStyleClass().setAll(user.isActive() ? "btn-danger" : "btn-success");
                toggle.setDisable(user == admin);
                setGraphic(toggle);
            }
        });

        accountsTable.getColumns().addAll(List.of(nameCol, roleCol, usernameCol, statusCol));
        accountsTable.setItems(FXCollections.observableArrayList(store.getAllUsers()));
        accountsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(accountsTable, Priority.ALWAYS);

        Button addButton = UI.primaryButton("+ Add Staff Account");
        addButton.setOnAction(e -> openAddAccountDialog());

        HBox toolbar = new HBox(10, (Node) UI.hSpacer(), addButton);

        VBox wrapper = new VBox(12, toolbar, accountsTable);
        wrapper.setPadding(new Insets(16, 0, 0, 0));
        return wrapper;
    }

    private String roleDetail(User user) {
        if (user instanceof Doctor) {
            return "Doctor - " + ((Doctor) user).getSpecialization();
        }
        if (user instanceof LabStaff) {
            return "Lab Staff - " + ((LabStaff) user).getSection();
        }
        return user.getRole().getDisplayName();
    }

    private void refreshAccountsTable() {
        accountsTable.setItems(FXCollections.observableArrayList(store.getAllUsers()));
    }

    private void openAddAccountDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add Staff Account");

        ComboBox<Role> roleBox = new ComboBox<>();
        roleBox.getItems().addAll(Role.DOCTOR, Role.NURSE_STAFF, Role.LAB_STAFF, Role.ADMIN);
        roleBox.getSelectionModel().selectFirst();

        TextField nameField = new TextField();
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();

        ComboBox<String> specializationBox = new ComboBox<>(FXCollections.observableArrayList(store.getDoctorCategories()));
        ComboBox<String> sectionBox = new ComboBox<>(FXCollections.observableArrayList(store.getLabSections()));

        VBox extraFieldContainer = new VBox(6);
        Runnable refreshExtraField = () -> {
            extraFieldContainer.getChildren().clear();
            if (roleBox.getValue() == Role.DOCTOR) {
                if (!specializationBox.getItems().isEmpty()) {
                    specializationBox.getSelectionModel().selectFirst();
                }
                extraFieldContainer.getChildren().add(UI.fieldGroup("Specialization", specializationBox));
            } else if (roleBox.getValue() == Role.LAB_STAFF) {
                if (!sectionBox.getItems().isEmpty()) {
                    sectionBox.getSelectionModel().selectFirst();
                }
                extraFieldContainer.getChildren().add(UI.fieldGroup("Laboratory Section", sectionBox));
            }
        };

        roleBox.setOnAction(e -> refreshExtraField.run());
        refreshExtraField.run();

        Label error = new Label();
        error.getStyleClass().add("login-error");
        error.setManaged(false);
        error.setVisible(false);

        Button cancel = UI.secondaryButton("Cancel");
        Button submit = UI.primaryButton("Create Account");
        cancel.setOnAction(e -> dialog.close());

        submit.setOnAction(e -> {
            String name = nameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
                showError(error, "Please fill in every field.");
                return;
            }
            if (password.length() < 4) {
                showError(error, "Password must be at least 4 characters long.");
                return;
            }
            if (store.usernameTaken(username)) {
                showError(error, "That username is already taken.");
                return;
            }
            Role role = roleBox.getValue();
            User newUser;
            switch (role) {
                case DOCTOR:
                    String specialization = specializationBox.getValue();
                    if (specialization == null) {
                        showError(error, "Please choose a specialization.");
                        return;
                    }
                    newUser = new Doctor(store.generateUserId("DR"), name, username, password, specialization);
                    break;
                case LAB_STAFF:
                    String section = sectionBox.getValue();
                    if (section == null) {
                        showError(error, "Please choose a laboratory section.");
                        return;
                    }
                    newUser = new LabStaff(store.generateUserId("LB"), name, username, password, section);
                    break;
                case ADMIN:
                    newUser = new Admin(store.generateUserId("AD"), name, username, password);
                    break;
                default:
                    newUser = new Nurse(store.generateUserId("NS"), name, username, password);
            }
            store.addUser(newUser);
            store.save();
            refreshAccountsTable();
            dialog.close();
        });

        HBox buttons = new HBox(10, (Node) UI.hSpacer(), cancel, submit);
        VBox root = new VBox(14,
                UI.pageTitle("Add Staff Account"),
                UI.fieldGroup("Role", roleBox),
                UI.fieldGroup("Full Name", nameField),
                UI.fieldGroup("Username", usernameField),
                UI.fieldGroup("Password", passwordField),
                extraFieldContainer,
                error,
                buttons);
        root.setPadding(new Insets(24));
        root.getStyleClass().add("root-bg");

        Scene scene = new Scene(root, 460, 560);
        scene.getStylesheets().add(getClass().getResource("/com/dlsu/medflow/styles.css").toExternalForm());
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showError(Label error, String message) {
        error.setText(message);
        error.setManaged(true);
        error.setVisible(true);
    }

    // -------------------------------------------------------------------
    // Doctor categories
    // -------------------------------------------------------------------

    private Node buildCategoriesTab() {
        categoriesList = new ListView<>(FXCollections.observableArrayList(store.getDoctorCategories()));
        categoriesList.setCellFactory(lv -> removableCell(text -> {
            store.removeDoctorCategory(text);
            store.save();
            refreshCategoriesList();
        }));
        VBox.setVgrow(categoriesList, Priority.ALWAYS);

        TextField newCategoryField = new TextField();
        newCategoryField.setPromptText("New specialization, e.g., Endocrinology");
        Button addButton = UI.primaryButton("Add Category");
        addButton.setOnAction(e -> {
            String value = newCategoryField.getText().trim();
            if (!value.isEmpty()) {
                store.addDoctorCategory(value);
                store.save();
                newCategoryField.clear();
                refreshCategoriesList();
            }
        });

        HBox addRow = new HBox(10, newCategoryField, addButton);
        VBox wrapper = new VBox(12, UI.muted("Used by the automatic Doctor Recommendation engine."), categoriesList, addRow);
        wrapper.setPadding(new Insets(16, 0, 0, 0));
        return wrapper;
}