package com.dlsu.medflow.gui.labStaff;

import com.dlsu.medflow.model.LabRequest;
import com.dlsu.medflow.model.LabStaff;
import com.dlsu.medflow.model.Visit;
import com.dlsu.medflow.model.VisitStatus;
import com.dlsu.medflow.service.HospitalDataStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        // Only requests routed to this laboratory section are displayed.
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

        model.addAttribute("labStaff", labStaff);
        model.addAttribute("pendingCount", pendingRequests.size());
        model.addAttribute("pendingRequests", pendingRequests);

        return "LabStaffDashboard";
    }

    @PostMapping("/lab/findings")
    public String submitFindings(
            @RequestParam String requestId,
            @RequestParam String findings,
            RedirectAttributes redirectAttributes) {

        LabStaff labStaff = store.getAllUsers().stream()
                .filter(user -> user instanceof LabStaff)
                .map(user -> (LabStaff) user)
                .findFirst()
                .orElse(null);

        String text = findings.trim();

        if (labStaff == null || text.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "findingsError",
                    "Please enter findings before submitting."
            );

            return "redirect:/lab";
        }

        HospitalDataStore.LabQueueItem item =
                store.getPendingLabRequests(labStaff.getSection())
                        .stream()
                        .filter(queueItem ->
                                queueItem.getLabRequest()
                                        .getRequestId()
                                        .equals(requestId))
                        .findFirst()
                        .orElse(null);

        if (item == null) {
            redirectAttributes.addFlashAttribute(
                    "findingsError",
                    "Laboratory request could not be found."
            );

            return "redirect:/lab";
        }

        Visit visit = item.getVisit();
        LabRequest request = item.getLabRequest();

        // UNDERSTAND:
        // This replaces the JavaFX Submit Findings button action.
        request.encodeFindings(text);

        // UNDERSTAND:
        // The visit is returned to the doctor only after
        // every laboratory request has findings encoded.
        if (visit.allFindingsEncoded()) {
            labStaff.updateStatus(
                    visit,
                    VisitStatus.FINDINGS_SENT_TO_DOCTOR
            );
        }

        store.save();

        redirectAttributes.addFlashAttribute(
                "findingsMessage",
                "Laboratory findings submitted."
        );

        return "redirect:/lab";
    }
}