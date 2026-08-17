package com.dlsu.medflow.web;

import com.dlsu.medflow.model.Doctor;
import com.dlsu.medflow.model.Visit;
import com.dlsu.medflow.model.VisitStatus;
import com.dlsu.medflow.service.HospitalDataStore;
import com.dlsu.medflow.web.support.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Replaces the "visit workspace" dialog that {@code DoctorDashboard} used to
 * pop open with {@code openWorkspace(visit)}. Here, the same set of actions
 * (begin assessment / save notes / request tests / review / release) are
 * separate {@code POST} endpoints that each redirect back to the workspace
 * page instead of leaving a JavaFX dialog open.
 */
@Controller
@RequestMapping("/doctor")
public class DoctorController {


    private final HospitalDataStore store;

    public DoctorController(HospitalDataStore store) {
        this.store = store;
    }

    @GetMapping("/visits/{visitId}")
    public String workspace(@PathVariable String visitId, HttpSession session, Model model) {
        Doctor doctor = (Doctor) session.getAttribute(SessionKeys.CURRENT_USER);
        Visit visit = store.getVisitById(visitId);
        if (visit == null || visit.getAssignedDoctor() != doctor) {
            return "redirect:/dashboard";
        }
        model.addAttribute("visit", visit);
        model.addAttribute("doctor", doctor);
        model.addAttribute("notes", visit.getMedicalRecord().getDoctorNotes(doctor));
        return "doctor/workspace";
    }

    private Doctor requireDoctor(HttpSession session) {
        return (Doctor) session.getAttribute(SessionKeys.CURRENT_USER);
    }

    private Visit requireOwnVisit(Doctor doctor, String visitId) {
        Visit visit = store.getVisitById(visitId);
        return (visit != null && visit.getAssignedDoctor() == doctor) ? visit : null;
    }
}