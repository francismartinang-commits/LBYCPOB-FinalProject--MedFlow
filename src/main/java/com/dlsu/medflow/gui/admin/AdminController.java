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

        long patientCount = store.getAllUsers()
                .stream()
                .filter(user -> user.getRole() == Role.PATIENT)
                .count();

        long doctorCount = store.getAllUsers()
                .stream()
                .filter(user -> user.getRole() == Role.DOCTOR)
                .count();

        long staffCount = store.getAllUsers()
                .stream()
                .filter(user ->
                        user.getRole() == Role.NURSE_STAFF
                                || user.getRole() == Role.LAB_STAFF)
                .count();

        long activeVisitCount = store.getAllVisits()
                .stream()
                .filter(visit ->
                        visit.getStatus() != VisitStatus.RELEASED_TO_PATIENT)
                .count();

        model.addAttribute("patientCount", patientCount);
        model.addAttribute("doctorCount", doctorCount);
        model.addAttribute("staffCount", staffCount);
        model.addAttribute("activeVisitCount", activeVisitCount);

        Map<VisitStatus, Long> visitsByStage = new LinkedHashMap<>();

        for (VisitStatus status : VisitStatus.values()) {

            long count = store.getAllVisits()
                    .stream()
                    .filter(visit -> visit.getStatus() == status)
                    .count();

            visitsByStage.put(status, count);
        }

        model.addAttribute("visitsByStage", visitsByStage);
        model.addAttribute("users", store.getAllUsers());

        Map<String, String> roleDetails = new LinkedHashMap<>();

        for (User user : store.getAllUsers()) {
            roleDetails.put(user.getUsername(), roleDetail(user));
        }

        model.addAttribute("roleDetails", roleDetails);

        model.addAttribute(
                "doctorCategories",
                store.getDoctorCategories()
        );

        // UNDERSTAND:
        // Laboratory sections are sent to AdminDashboard.html so the
        // admin can view and manage the available lab sections.
        model.addAttribute(
                "labSections",
                store.getLabSections()
        );

        return "AdminDashboard";
    }

    @PostMapping("/admin/accounts/toggle")
    public String toggleAccount(@RequestParam String username) {

        User user = store.getAllUsers()
                .stream()
                .filter(account ->
                        account.getUsername().equals(username))
                .findFirst()
                .orElse(null);

        if (user != null) {

            if (user.getRole() != Role.ADMIN) {
                user.setActive(!user.isActive());
                store.save();
            }
        }

        return "redirect:/admin";
    }

    @PostMapping("/admin/categories/add")
    public String addDoctorCategory(@RequestParam String category) {

        String value = category.trim();

        if (!value.isEmpty()) {
            store.addDoctorCategory(value);
            store.save();
        }

        return "redirect:/admin";
    }

    @PostMapping("/admin/categories/remove")
    public String removeDoctorCategory(@RequestParam String category) {

        store.removeDoctorCategory(category);
        store.save();

        return "redirect:/admin";
    }

    // UNDERSTAND:
    // This receives a new laboratory section from AdminDashboard.html
    // and adds it to the stored list.
    @PostMapping("/admin/sections/add")
    public String addLabSection(@RequestParam String section) {

        String value = section.trim();

        // DECISION:
        // Empty laboratory section names are ignored.
        if (!value.isEmpty()) {
            store.addLabSection(value);
            store.save();
        }

        return "redirect:/admin";
    }

    // UNDERSTAND:
    // This removes the laboratory section selected by the admin.
    @PostMapping("/admin/sections/remove")
    public String removeLabSection(@RequestParam String section) {

        store.removeLabSection(section);
        store.save();

        // AI-CHECK:
        // The JavaFX removable ListView behavior is replaced with
        // a Spring Boot POST request.
        return "redirect:/admin";
    }

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