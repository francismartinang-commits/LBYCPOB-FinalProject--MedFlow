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

}
