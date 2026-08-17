package com.dlsu.medflow.web;

import com.dlsu.medflow.model.*;
import com.dlsu.medflow.service.HospitalDataStore;
import com.dlsu.medflow.web.support.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


/** Replaces the "Manage Accounts", "Doctor Categories", and "Laboratory Sections" tabs of {@code AdminDashboard}. */
@Controller
@RequestMapping("/admin")
public class AdminController {


    private final HospitalDataStore store;

    public AdminController(HospitalDataStore store) {
        this.store = store;
    }


    @GetMapping("/accounts/new")
    public String newAccountForm(Model model) {
        model.addAttribute("categories", store.getDoctorCategories());
        model.addAttribute("sections", store.getLabSections());
        return "admin/add-account";
    }

    @PostMapping("/accounts")
    public String createAccount(@RequestParam Role role, @RequestParam String name, @RequestParam String username,
                                @RequestParam String password,
                                @RequestParam(required = false) String specialization,
                                @RequestParam(required = false) String section,
                                Model model) {

        if (name.isBlank() || username.isBlank() || password.isBlank()) {
            return accountError(model, "Please fill in every field.");
        }
        if (password.length() < 4) {
            return accountError(model, "Password must be at least 4 characters long.");
        }
        if (store.usernameTaken(username.trim())) {
            return accountError(model, "That username is already taken.");
        }


        User newUser;
        switch (role) {
            case DOCTOR -> {
                if (specialization == null || specialization.isBlank()) {
                    return accountError(model, "Please choose a specialization.");
                }
                newUser = new Doctor(store.generateUserId("DR"), name.trim(), username.trim(), password, specialization);
            }
            case LAB_STAFF -> {
                if (section == null || section.isBlank()) {
                    return accountError(model, "Please choose a laboratory section.");
                }
                newUser = new LabStaff(store.generateUserId("LB"), name.trim(), username.trim(), password, section);
            }
            case ADMIN -> newUser = new Admin(store.generateUserId("AD"), name.trim(), username.trim(), password);
            default -> newUser = new Nurse(store.generateUserId("NS"), name.trim(), username.trim(), password);
        }

}
