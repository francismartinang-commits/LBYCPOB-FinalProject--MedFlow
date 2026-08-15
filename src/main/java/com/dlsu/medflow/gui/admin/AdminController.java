package com.dlsu.medflow.gui.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    // UNDERSTAND:
    // @GetMapping connects the "/admin" URL to this method.
    @GetMapping("/admin")
    public String showAdminDashboard(Model model) {

        // DECISION:
        // The Model sends values from the controller to AdminDashboard.html.
        model.addAttribute(
                "pageTitle",
                "Admin Dashboard"
        );

        model.addAttribute(
                "pageSubtitle",
                "Manage accounts, doctor categories, and laboratory sections."
        );

        // AI-CHECK:
        // Instead of returning a JavaFX Parent, Spring Boot returns the name
        // of the Thymeleaf page that should be displayed.
        return "AdminDashboard";
    }
}