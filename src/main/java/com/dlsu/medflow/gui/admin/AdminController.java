package com.dlsu.medflow.gui.admin;

import com.dlsu.medflow.model.Doctor;
import com.dlsu.medflow.model.LabStaff;
import com.dlsu.medflow.model.Role;
import com.dlsu.medflow.model.User;
import com.dlsu.medflow.model.VisitStatus;
import com.dlsu.medflow.service.HospitalDataStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.Map;

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

        model.addAttribute("patientCount", patientCount);
        model.addAttribute("doctorCount", doctorCount);
        model.addAttribute("staffCount", staffCount);
        model.addAttribute("activeVisitCount", activeVisitCount);

        // UNDERSTAND:
        // This counts how many visits are currently under each
        // VisitStatus.
        Map<VisitStatus, Long> visitsByStage = new LinkedHashMap<>();

        for (VisitStatus status : VisitStatus.values()) {

            long count = store.getAllVisits()
                    .stream()
                    .filter(visit -> visit.getStatus() == status)
                    .count();

            visitsByStage.put(status, count);
        }

        // DECISION:
        // LinkedHashMap keeps the stages in their proper workflow order.
        model.addAttribute("visitsByStage", visitsByStage);

        // UNDERSTAND:
        // Send all registered users to the accounts table.
        model.addAttribute("users", store.getAllUsers());

        // UNDERSTAND:
        // Give Doctor and Lab Staff accounts a more detailed role
        // description.
        Map<String, String> roleDetails = new LinkedHashMap<>();

        for (User user : store.getAllUsers()) {
            roleDetails.put(user.getUsername(), roleDetail(user));
        }

        model.addAttribute("roleDetails", roleDetails);

        // UNDERSTAND:
        // Doctor categories are sent to AdminDashboard.html so the
        // admin can view and manage the available specializations.
        model.addAttribute(
                "doctorCategories",
                store.getDoctorCategories()
        );

        // AI-CHECK:
        // The JavaFX AdminDashboard logic is being separated between
        // AdminController and AdminDashboard.html for Spring Boot.
        return "AdminDashboard";
    }

    // UNDERSTAND:
    // This method handles the Activate or Deactivate button from
    // AdminDashboard.html.
    @PostMapping("/admin/accounts/toggle")
    public String toggleAccount(@RequestParam String username) {

        User user = store.getAllUsers()
                .stream()
                .filter(account ->
                        account.getUsername().equals(username))
                .findFirst()
                .orElse(null);

        // DECISION:
        // Only change the account if it actually exists.
        if (user != null) {

            // DECISION:
            // Admin accounts are protected for now because authentication
            // has not yet been converted and the controller cannot identify
            // which admin is currently logged in.
            if (user.getRole() != Role.ADMIN) {
                user.setActive(!user.isActive());
                store.save();
            }
        }

        // UNDERSTAND:
        // Redirect back to /admin after changing the account so the
        // refreshed account status is displayed.
        return "redirect:/admin";
    }

    // UNDERSTAND:
    // This receives a new doctor specialization from AdminDashboard.html
    // and adds it to the stored list of doctor categories.
    @PostMapping("/admin/categories/add")
    public String addDoctorCategory(@RequestParam String category) {

        String value = category.trim();

        // DECISION:
        // Empty category names are ignored so blank values are not stored.
        if (!value.isEmpty()) {
            store.addDoctorCategory(value);
            store.save();
        }

        return "redirect:/admin";
    }

    // UNDERSTAND:
    // This receives the selected doctor category from AdminDashboard.html
    // and removes it from the stored list.
    @PostMapping("/admin/categories/remove")
    public String removeDoctorCategory(@RequestParam String category) {

        store.removeDoctorCategory(category);
        store.save();

        // AI-CHECK:
        // The JavaFX removable ListView behavior is replaced with
        // a Spring Boot POST request.
        return "redirect:/admin";
    }

    // UNDERSTAND:
    // Doctors and laboratory staff contain additional information
    // connected to their role.
    private String roleDetail(User user) {

        if (user instanceof Doctor doctor) {
            return "Doctor - " + doctor.getSpecialization();
        }

        if (user instanceof LabStaff labStaff) {
            return "Lab Staff - " + labStaff.getSection();
        }

        return user.getRole().getDisplayName();
    }
}