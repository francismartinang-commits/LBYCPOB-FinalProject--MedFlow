package com.dlsu.medflow.web;

import com.dlsu.medflow.model.Patient;
import com.dlsu.medflow.service.HospitalDataStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.dlsu.medflow.model.Doctor;
import com.dlsu.medflow.model.Nurse;
import com.dlsu.medflow.model.User;
import com.dlsu.medflow.model.Visit;
import com.dlsu.medflow.model.VisitStatus;
import com.dlsu.medflow.web.support.SessionKeys;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;



/** Replaces the three tabs of {@code NurseDashboard}, plus its walk-in registration dialog. */
@Controller
@RequestMapping("/nurse")
public class NurseController {
    private final HospitalDataStore store;

    public NurseController(HospitalDataStore store) {
        this.store = store;
    }

    @GetMapping("/walkin")
    public String walkInForm(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new HashMap<String, String>());
        }
        return "nurse/walkin";
    }
    /** Same validation as the public /register form - a nurse is filling it in on the walk-in patient's behalf. */
    @PostMapping("/walkin")
    public String walkIn(@RequestParam String name, @RequestParam String age, @RequestParam String gender,
                         @RequestParam String contactNumber, @RequestParam String address,
                         @RequestParam String username, @RequestParam String password,
                         @RequestParam String reason, Model model) {

        if (isBlank(name) || isBlank(age) || isBlank(contactNumber) || isBlank(address)
                || isBlank(username) || isBlank(password) || isBlank(reason)) {
            return walkInError(model, "Please fill in every field before submitting.",
                    name, age, gender, contactNumber, address, username, reason);
        }
        int parsedAge;
        try {
            parsedAge = Integer.parseInt(age.trim());
            if (parsedAge <= 0 || parsedAge > 130) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            return walkInError(model, "Please enter a valid age.",
                    name, age, gender, contactNumber, address, username, reason);
        }
        if (password.length() < 4) {
            return walkInError(model, "Password must be at least 4 characters long.",
                    name, age, gender, contactNumber, address, username, reason);
        }
        if (store.usernameTaken(username.trim())) {
            return walkInError(model, "That username is already taken - please choose another.",
                    name, age, gender, contactNumber, address, username, reason);
        }

        Patient patient = store.registerPatient(name.trim(), parsedAge, gender, contactNumber.trim(),
                address.trim(), username.trim(), password);
        store.registerVisit(patient, reason.trim());
        store.save();
        return "redirect:/dashboard";
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private String walkInError(Model model, String message, String name, String age, String gender,
                               String contactNumber, String address, String username, String reason) {
        model.addAttribute("errorMessage", message);
        var form = new HashMap<String, String>();
        form.put("name", name);
        form.put("age", age);
        form.put("gender", gender);
        form.put("contactNumber", contactNumber);
        form.put("address", address);
        form.put("username", username);
        form.put("reason", reason);
        model.addAttribute("form", form);
        return "nurse/walkin";
    }

    @PostMapping("/registrations/{visitId}/confirm")
    public String confirmAssignment(@PathVariable String visitId, @RequestParam String doctorId, HttpSession session) {
        Nurse nurse = (Nurse) session.getAttribute(SessionKeys.CURRENT_USER);
        Visit visit = store.getVisitById(visitId);
        User chosen = store.getUserById(doctorId);
        if (visit != null && chosen instanceof Doctor doctor) {
            visit.setAssignedDoctor(doctor);
            nurse.updateStatus(visit, VisitStatus.ASSIGNED_TO_DOCTOR);
            store.save();
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/samples/{visitId}/confirm")
    public String confirmSample(@PathVariable String visitId, HttpSession session) {
        Nurse nurse = (Nurse) session.getAttribute(SessionKeys.CURRENT_USER);
        Visit visit = store.getVisitById(visitId);
        if (visit != null) {
            nurse.updateStatus(visit, VisitStatus.SAMPLE_COLLECTED);
            store.save();
        }
        return "redirect:/dashboard";
    }

    // Added inside NurseController:

    /** Sends to the laboratory AND immediately marks it under analysis, exactly like the JavaFX "Send" button did. */
    @PostMapping("/send/{visitId}")
    public String sendToLaboratory(@PathVariable String visitId, HttpSession session) {
        Nurse nurse = (Nurse) session.getAttribute(SessionKeys.CURRENT_USER);
        Visit visit = store.getVisitById(visitId);
        if (visit != null) {
            nurse.updateStatus(visit, VisitStatus.SENT_TO_LABORATORY);
            nurse.updateStatus(visit, VisitStatus.UNDER_LABORATORY_ANALYSIS);
            store.save();
        }
        return "redirect:/dashboard";
    }
}