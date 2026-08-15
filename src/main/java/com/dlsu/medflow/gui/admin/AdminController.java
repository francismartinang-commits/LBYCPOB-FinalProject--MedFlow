package com.dlsu.medflow.gui.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    // UNDERSTAND:
    // @GetMapping connects the "/admin" URL to this method.
    // When the user goes to /admin, Spring Boot runs showAdminDashboard().

    // DECISION:
    // Model is used to send data from the controller to the HTML page
    // instead of directly creating a JavaFX dashboard.
    @GetMapping("/admin")
    public String showAdminDashboard(Model model) {

        // UNDERSTAND:
        // These values are added to the Model so they can be displayed
        // inside the Thymeleaf admin dashboard page.
        model.addAttribute(
                "pageTitle",
                "Admin Dashboard"
        );

        model.addAttribute(
                "pageSubtitle",
                "Manage accounts, doctor categories, and laboratory sections."
        );

        // AI-CHECK:
        // The JavaFX AdminDashboard is replaced by a Thymeleaf template.
        // Returning "admin-dashboard" tells Spring Boot to open
        // admin-dashboard.html from the templates folder.
        return "admin-dashboard";
    }
}