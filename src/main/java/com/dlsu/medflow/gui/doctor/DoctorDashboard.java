package com.dlsu.medflow.gui.doctor;

import com.dlsu.medflow.gui.components.StatusTrackerView;
import com.dlsu.medflow.model.Doctor;
import com.dlsu.medflow.model.Visit;
import com.dlsu.medflow.model.VisitStatus;
import com.dlsu.medflow.service.HospitalDataStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DoctorDashboard {

    private final HospitalDataStore store;
    private final StatusTrackerView statusTrackerView;

    // UNDERSTAND:
    // Spring injects the data store and reusable status tracker component
    // instead of the JavaFX dashboard creating UI objects directly.
    public DoctorDashboard(
            HospitalDataStore store,
            StatusTrackerView statusTrackerView) {

        this.store = store;
        this.statusTrackerView = statusTrackerView;
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

    @GetMapping("/doctor/visit")
    public String showVisitWorkspace(
            @RequestParam String visitId,
            Model model) {

        // UNDERSTAND:
        // The JavaFX version opened a Stage for the selected visit.
        // Spring MVC instead opens a separate Thymeleaf page.
        Visit visit = store.getAllVisits().stream()
                .filter(v -> v.getVisitId().equals(visitId))
                .findFirst()
                .orElse(null);

        if (visit == null) {
            model.addAttribute(
                    "errorMessage",
                    "Visit could not be found."
            );
            return "DoctorVisit";
        }

        Doctor doctor = visit.getAssignedDoctor();

        model.addAttribute(
                "pageTitle",
                visit.getPatient().getName()
        );

        model.addAttribute("visit", visit);
        model.addAttribute("doctor", doctor);
        model.addAttribute("patient", visit.getPatient());

        // UNDERSTAND:
        // These replace the JavaFX Clinical Notes and
        // Laboratory Requests sections.
        model.addAttribute(
                "clinicalNotes",
                visit.getMedicalRecord().getDoctorNotes(doctor)
        );

        model.addAttribute(
                "labRequests",
                visit.getLabRequests()
        );

        model.addAttribute(
                "statusRows",
                statusTrackerView.buildTracker(visit)
        );

        return "DoctorVisit";
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