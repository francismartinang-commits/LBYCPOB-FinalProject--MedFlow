package com.dlsu.medflow.gui.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

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

        return "admin-dashboard";
    }
}