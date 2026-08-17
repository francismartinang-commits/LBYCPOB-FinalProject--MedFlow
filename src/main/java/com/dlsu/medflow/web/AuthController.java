package com.dlsu.medflow.web;

import com.dlsu.medflow.model.Patient;
import com.dlsu.medflow.model.User;
import com.dlsu.medflow.service.HospitalDataStore;
import com.dlsu.medflow.web.support.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Replaces {@code LoginView} and {@code PatientRegistrationDialog} from the
 * JavaFX edition. Same validation rules, just triggered by an HTTP POST
 * instead of a button's {@code setOnAction}.
 */
@Controller
public class AuthController {

    /**
     * Mirrors LoginView's demo-account quick-fill panel.
     */
    private static final List<String[]> DEMO_ACCOUNTS = List.of(
            new String[]{"Admin", "admin", "admin123"},
            new String[]{"Doctor - Dr. Ana Reyes (General Medicine)", "dr.reyes", "doctor123"},
            new String[]{"Doctor - Dr. Miguel Santos (Cardiology)", "dr.santos", "doctor123"},
            new String[]{"Nurse / Staff - Liza Ramos", "nurse.ramos", "nurse123"},
            new String[]{"Laboratory Staff - Jun Dizon (Hematology)", "lab.dizon", "lab123"},
            new String[]{"Laboratory Staff - Ella Manalo (Chemistry / Biochemistry)", "lab.manalo", "lab123"},
            new String[]{"Patient - Juan Dela Cruz", "patient.juan", "patient123"}
    );

    private final HospitalDataStore store;

    public AuthController(HospitalDataStore store) {
        this.store = store;
    }

    @GetMapping("/")
    public String root(HttpSession session) {
        boolean loggedIn = session.getAttribute(SessionKeys.CURRENT_USER) != null;
        return loggedIn ? "redirect:/dashboard" : "redirect:/login";
    }


    @GetMapping("/login")
    public String loginForm(HttpSession session,
                            @RequestParam(required = false) String registered,
                            @RequestParam(required = false) String username,
                            Model model) {
        if (session.getAttribute(SessionKeys.CURRENT_USER) != null) {
            return "redirect:/dashboard";
        }
        if (registered != null) {
            model.addAttribute("successMessage", "Registration successful! Enter your password to log in.");
            model.addAttribute("prefillUsername", username);
        }
        model.addAttribute("demoAccounts", DEMO_ACCOUNTS);
        return "login";
    }


    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password,
                        HttpSession session, Model model) {
        model.addAttribute("demoAccounts", DEMO_ACCOUNTS);
        if (username.isBlank() || password.isBlank()) {
            model.addAttribute("errorMessage", "Please enter both your username and password.");
            model.addAttribute("prefillUsername", username);
            return "login";
        }
        User user = store.authenticate(username, password);
        if (user == null) {
            model.addAttribute("errorMessage", "Incorrect username or password. Please try again.");
            model.addAttribute("prefillUsername", username);
            return "login";
        }
        session.setAttribute(SessionKeys.CURRENT_USER, user);
        return "redirect:/dashboard";
    }
}




