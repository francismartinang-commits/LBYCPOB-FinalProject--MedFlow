import com.dlsu.medflow.model.Patient;
import com.dlsu.medflow.service.HospitalDataStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
}