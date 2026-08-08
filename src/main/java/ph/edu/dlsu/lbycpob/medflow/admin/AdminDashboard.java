package ph.edu.dlsu.lbycpob.medflow.admin;

import ph.edu.dlsu.lbycpob.medflow.gui.components.UI;
import ph.edu.dlsu.lbycpob.medflow.model.Admin;
import ph.edu.dlsu.lbycpob.medflow.service.HospitalDataStore;


import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;

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

        Tab overviewTab =
                new Tab(
                        "Overview",
                        new Label("Admin overview")
                );

        Tab accountsTab =
                new Tab(
                        "Manage Accounts",
                        new Label("Account management")
                );

        Tab categoriesTab =
                new Tab(
                        "Doctor Categories",
                        new Label("Doctor category management")
                );

        Tab sectionsTab =
                new Tab(
                        "Laboratory Sections",
                        new Label("Laboratory section management")
                );

        overviewTab.setClosable(false);
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
}