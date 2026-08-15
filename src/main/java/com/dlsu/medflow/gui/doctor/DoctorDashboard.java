package com.dlsu.medflow.gui.doctor;

import com.dlsu.medflow.model.Doctor;
import com.dlsu.medflow.model.Visit;
import com.dlsu.medflow.model.VisitStatus;
import com.dlsu.medflow.service.HospitalDataStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DoctorDashboard {

    private final HospitalDataStore store;

    // UNDERSTAND:
    // The JavaFX version created VBox, HBox, Labels, and cards.
    // Spring MVC now prepares the dashboard data for Thymeleaf.
    public DoctorDashboard(HospitalDataStore store) {
        this.store = store;
    }

    @GetMapping("/doctor")
    public String showDashboard(Model model) {

        // DECISION:
        // Login/session handling will be connected when authentication
        // reaches its own conversion step.
        Doctor doctor = store.getAllUsers().stream()
                .filter(user -> user instanceof Doctor)
                .map(user -> (Doctor) user)
                .findFirst()
                .orElse(null);

        if (doctor == null) {
            model.addAttribute(
                    "errorMessage",
                    "No doctor account available."
            );
            return "DoctorDashboard";
        }

        List<Visit> visits = store.getVisitsForDoctor(doctor);

        visits.sort((a, b) -> Boolean.compare(
                a.getStatus() == VisitStatus.RELEASED_TO_PATIENT,
                b.getStatus() == VisitStatus.RELEASED_TO_PATIENT
        ));

        long awaitingAssessment = visits.stream()
                .filter(v -> v.getStatus()
                        == VisitStatus.ASSIGNED_TO_DOCTOR)
                .count();

        long awaitingFindings = visits.stream()
                .filter(v -> v.getStatus()
                        == VisitStatus.FINDINGS_SENT_TO_DOCTOR)
                .count();

        long inProgress = visits.stream()
                .filter(v -> v.getStatus()
                        != VisitStatus.RELEASED_TO_PATIENT)
                .count();

        long completed = visits.stream()
                .filter(v -> v.getStatus()
                        == VisitStatus.RELEASED_TO_PATIENT)
                .count();

        model.addAttribute(
                "pageTitle",
                "Dr. " + lastNameOnly(doctor.getName()) + "'s Dashboard"
        );

        model.addAttribute(
                "pageSubtitle",
                doctor.getSpecialization()
                        + " · Assigned patients, assessments, and laboratory requests."
        );

        model.addAttribute("doctor", doctor);
        model.addAttribute("visits", visits);
        model.addAttribute("inProgress", inProgress);
        model.addAttribute("awaitingAssessment", awaitingAssessment);
        model.addAttribute("awaitingFindings", awaitingFindings);
        model.addAttribute("completed", completed);

        return "DoctorDashboard";
    }

    public String actionLabelFor(VisitStatus status) {
        return switch (status) {
            case ASSIGNED_TO_DOCTOR -> "Begin Assessment";
            case FINDINGS_SENT_TO_DOCTOR -> "Review Findings";
            case DOCTOR_REVIEWED -> "Release to Patient";
            case RELEASED_TO_PATIENT -> "View Record";
            default -> "Open";
        };
    }

    private String lastNameOnly(String fullName) {
        String withoutTitle = fullName.replace("Dr. ", "");
        String[] parts = withoutTitle.split(" ");
        return parts[parts.length - 1];
    }
}