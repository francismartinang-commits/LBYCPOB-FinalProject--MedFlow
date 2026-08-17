package com.dlsu.medflow.web;

import com.dlsu.medflow.model.Patient;
import com.dlsu.medflow.service.HospitalDataStore;
import com.dlsu.medflow.web.support.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/** Replaces the "+ Register a New Visit" dialog and the timeline dialog from {@code PatientDashboard}. */
@Controller
@RequestMapping("/patient")
public class PatientController {

    private final HospitalDataStore store;

    public PatientController(HospitalDataStore store) {
        this.store = store;
    }

    @GetMapping("/visits/new")
    public String newVisitForm() {
        return "patient/new-visit";
    }

    @PostMapping("/visits")
    public String createVisit(@RequestParam String reason, HttpSession session, Model model) {
        Patient patient = (Patient) session.getAttribute(SessionKeys.CURRENT_USER);
        if (reason == null || reason.isBlank()) {
            model.addAttribute("errorMessage", "Please describe your reason for visit.");
            return "patient/new-visit";
        }
        store.registerVisit(patient, reason.trim());
        store.save();
        return "redirect:/dashboard";
    }
}