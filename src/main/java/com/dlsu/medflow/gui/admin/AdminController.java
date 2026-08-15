package com.dlsu.medflow.gui.admin;

import com.dlsu.medflow.model.Admin;
import com.dlsu.medflow.model.Doctor;
import com.dlsu.medflow.model.LabStaff;
import com.dlsu.medflow.model.Nurse;
import com.dlsu.medflow.model.Role;
import com.dlsu.medflow.model.User;
import com.dlsu.medflow.model.VisitStatus;
import com.dlsu.medflow.service.HospitalDataStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        model.addAttribute("pageTitle", "Admin Dashboard");
        model.addAttribute(
                "pageSubtitle",
                "Manage accounts, doctor categories, and laboratory sections."
        );

        long patientCount = store.getAllUsers().stream()
                .filter(user -> user.getRole() == Role.PATIENT)
                .count();

        long doctorCount = store.getAllUsers().stream()
                .filter(user -> user.getRole() == Role.DOCTOR)
                .count();

        long staffCount = store.getAllUsers().stream()
                .filter(user ->
                        user.getRole() == Role.NURSE_STAFF
                                || user.getRole() == Role.LAB_STAFF)
                .count();

        long activeVisitCount = store.getAllVisits().stream()
                .filter(visit ->
                        visit.getStatus() != VisitStatus.RELEASED_TO_PATIENT)
                .count();

        model.addAttribute("patientCount", patientCount);
        model.addAttribute("doctorCount", doctorCount);
        model.addAttribute("staffCount", staffCount);
        model.addAttribute("activeVisitCount", activeVisitCount);

        Map<VisitStatus, Long> visitsByStage = new LinkedHashMap<>();

        for (VisitStatus status : VisitStatus.values()) {
            long count = store.getAllVisits().stream()
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
        model.addAttribute("doctorCategories", store.getDoctorCategories());
        model.addAttribute("labSections", store.getLabSections());

        // UNDERSTAND:
        // These are the roles an admin is allowed to create.
        model.addAttribute(
                "staffRoles",
                new Role[]{
                        Role.DOCTOR,
                        Role.NURSE_STAFF,
                        Role.LAB_STAFF,
                        Role.ADMIN
                }
        );

        return "AdminDashboard";
    }

    @PostMapping("/admin/accounts/toggle")
    public String toggleAccount(@RequestParam String username) {

        User user = store.getAllUsers().stream()
                .filter(account -> account.getUsername().equals(username))
                .findFirst()
                .orElse(null);

        if (user != null && user.getRole() != Role.ADMIN) {
            user.setActive(!user.isActive());
            store.save();
        }

        return "redirect:/admin";
    }

    // UNDERSTAND:
    // This receives the information entered in the Add Staff Account form.
    @PostMapping("/admin/accounts/add")
    public String addStaffAccount(
            @RequestParam Role role,
            @RequestParam String name,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String section,
            RedirectAttributes redirectAttributes) {

        name = name.trim();
        username = username.trim();

        // DECISION:
        // Required information must be completed before an account is created.
        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Please fill in every field.");
            return "redirect:/admin";
        }

        if (password.length() < 4) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Password must be at least 4 characters long.");
            return "redirect:/admin";
        }

        if (store.usernameTaken(username)) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "That username is already taken.");
            return "redirect:/admin";
        }

        User newUser;

        // UNDERSTAND:
        // The selected role determines which User subclass is created.
        switch (role) {
            case DOCTOR -> {
                if (specialization == null || specialization.isBlank()) {
                    redirectAttributes.addFlashAttribute(
                            "errorMessage", "Please choose a specialization.");
                    return "redirect:/admin";
                }

                newUser = new Doctor(
                        store.generateUserId("DR"),
                        name, username, password, specialization);
            }

            case LAB_STAFF -> {
                if (section == null || section.isBlank()) {
                    redirectAttributes.addFlashAttribute(
                            "errorMessage",
                            "Please choose a laboratory section.");
                    return "redirect:/admin";
                }

                newUser = new LabStaff(
                        store.generateUserId("LB"),
                        name, username, password, section);
            }

            case ADMIN -> newUser = new Admin(
                    store.generateUserId("AD"),
                    name, username, password);

            default -> newUser = new Nurse(
                    store.generateUserId("NS"),
                    name, username, password);
        }

        store.addUser(newUser);
        store.save();

        // AI-CHECK:
        // The JavaFX account creation dialog is replaced by a POST request.
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

    @PostMapping("/admin/sections/add")
    public String addLabSection(@RequestParam String section) {

        String value = section.trim();

        if (!value.isEmpty()) {
            store.addLabSection(value);
            store.save();
        }

        return "redirect:/admin";
    }

    @PostMapping("/admin/sections/remove")
    public String removeLabSection(@RequestParam String section) {

        store.removeLabSection(section);
        store.save();

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