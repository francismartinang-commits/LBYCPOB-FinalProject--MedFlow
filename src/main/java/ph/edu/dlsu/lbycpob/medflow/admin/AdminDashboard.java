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
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

import java.util.List;

private VBox overviewStats;

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
                        buildOverviewTab()
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
}