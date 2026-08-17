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

    @GetMapping("/register")
    public String registerForm(HttpSession session, Model model) {
        if (session.getAttribute(SessionKeys.CURRENT_USER) != null) {
            return "redirect:/dashboard";
        }
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new HashMap<String, String>());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name, @RequestParam String age, @RequestParam String gender,
                           @RequestParam String contactNumber, @RequestParam String address,
                           @RequestParam String username, @RequestParam String password,
                           @RequestParam String reason, Model model) {

        if (isBlank(name) || isBlank(age) || isBlank(contactNumber) || isBlank(address)
                || isBlank(username) || isBlank(password) || isBlank(reason)) {
            return registerError(model, "Please fill in every field before submitting.",
                    name, age, gender, contactNumber, address, username, reason);
        }

        int parsedAge;
        try {
            parsedAge = Integer.parseInt(age.trim());
            if (parsedAge <= 0 || parsedAge > 130) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            return registerError(model, "Please enter a valid age.",
                    name, age, gender, contactNumber, address, username, reason);
        }

        if (password.length() < 4) {
            return registerError(model, "Password must be at least 4 characters long.",
                    name, age, gender, contactNumber, address, username, reason);
        }

        if (store.usernameTaken(username.trim())) {
            return registerError(model, "That username is already taken - please choose another.",
                    name, age, gender, contactNumber, address, username, reason);
        }

        Patient patient = store.registerPatient(name.trim(), parsedAge, gender, contactNumber.trim(),
                address.trim(), username.trim(), password);
        store.registerVisit(patient, reason.trim());
        store.save();

        return "redirect:/login?registered=true&username=" + username.trim();
    }
    // Helper methods for registration
    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private String registerError(Model model, String message, String name, String age, String gender,
                                 String contactNumber, String address, String username, String reason) {
        model.addAttribute("errorMessage", message);
        Map<String, String> form = new HashMap<>();
        form.put("name", name);
        form.put("age", age);
        form.put("gender", gender);
        form.put("contactNumber", contactNumber);
        form.put("address", address);
        form.put("username", username);
        form.put("reason", reason);
        model.addAttribute("form", form);
        return "register";
    }
}




