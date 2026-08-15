package com.dlsu.medflow.gui.admin;

import com.dlsu.medflow.model.Role;
import com.dlsu.medflow.model.VisitStatus;
import com.dlsu.medflow.service.HospitalDataStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final HospitalDataStore store;

    // UNDERSTAND:
    // The controller needs access to HospitalDataStore so it can get
    // the users and visits that will be displayed in AdminDashboard.html.
    public AdminController(HospitalDataStore store) {
        this.store = store;
    }

    @GetMapping("/admin")
    public String showAdminDashboard(Model model) {

        model.addAttribute(
                "pageTitle",
                "Admin Dashboard"
        );

        model.addAttribute(
                "pageSubtitle",
                "Manage accounts, doctor categories, and laboratory sections."
        );

        // UNDERSTAND:
        // Count all registered users that have the PATIENT role.
        long patientCount = store.getAllUsers()
                .stream()
                .filter(user -> user.getRole() == Role.PATIENT)
                .count();

        // UNDERSTAND:
        // Count all registered users that have the DOCTOR role.
        long doctorCount = store.getAllUsers()
                .stream()
                .filter(user -> user.getRole() == Role.DOCTOR)
                .count();

        // UNDERSTAND:
        // Nurses and laboratory staff are grouped together because the
        // original AdminDashboard displays them as one statistic.
        long staffCount = store.getAllUsers()
                .stream()
                .filter(user ->
                        user.getRole() == Role.NURSE_STAFF
                                || user.getRole() == Role.LAB_STAFF)
                .count();

        // DECISION:
        // A visit is considered active until it reaches
        // RELEASED_TO_PATIENT, which is the last stage of the workflow.
        long activeVisitCount = store.getAllVisits()
                .stream()
                .filter(visit ->
                        visit.getStatus() != VisitStatus.RELEASED_TO_PATIENT)
                .count();

        // UNDERSTAND:
        // These values are passed to the Thymeleaf page through Model.
        model.addAttribute("patientCount", patientCount);
        model.addAttribute("doctorCount", doctorCount);
        model.addAttribute("staffCount", staffCount);
        model.addAttribute("activeVisitCount", activeVisitCount);

        // AI-CHECK:
        // The overview logic was moved from the JavaFX AdminDashboard
        // into the Spring Boot controller. AdminDashboard.html now
        // handles only how these values are displayed.
        return "AdminDashboard";
    }
}