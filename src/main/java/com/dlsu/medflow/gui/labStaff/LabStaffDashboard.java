package com.dlsu.medflow.gui.labStaff;

import com.dlsu.medflow.model.LabStaff;
import com.dlsu.medflow.service.HospitalDataStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class LabStaffDashboard {

    private final HospitalDataStore store;

    public LabStaffDashboard(HospitalDataStore store) {
        this.store = store;
    }

    @GetMapping("/lab")
    public String showDashboard(Model model) {

        // DECISION:
        // Until login/session handling is converted,
        // the first LabStaff account is used temporarily.
        LabStaff labStaff = store.getAllUsers().stream()
                .filter(user -> user instanceof LabStaff)
                .map(user -> (LabStaff) user)
                .findFirst()
                .orElse(null);

        if (labStaff == null) {
            model.addAttribute(
                    "errorMessage",
                    "No laboratory staff account available."
            );

            return "LabStaffDashboard";
        }

        // UNDERSTAND:
        // This replaces the JavaFX refreshQueue() behavior.
        // Only requests routed to the staff member's section are loaded.
        List<HospitalDataStore.LabQueueItem> pendingRequests =
                store.getPendingLabRequests(
                        labStaff.getSection()
                );

        model.addAttribute(
                "pageTitle",
                labStaff.getSection() + " Queue"
        );

        model.addAttribute(
                "pageSubtitle",
                "Requests routed to your section, waiting to be encoded."
        );

        model.addAttribute(
                "labStaff",
                labStaff
        );

        model.addAttribute(
                "pendingCount",
                pendingRequests.size()
        );

        model.addAttribute(
                "pendingRequests",
                pendingRequests
        );

        return "LabStaffDashboard";
    }
}