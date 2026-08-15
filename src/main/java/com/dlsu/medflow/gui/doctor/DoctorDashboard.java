package com.dlsu.medflow.gui.doctor;

import com.dlsu.medflow.gui.components.StatusTrackerView;
import com.dlsu.medflow.model.Doctor;
import com.dlsu.medflow.model.Priority;
import com.dlsu.medflow.model.Visit;
import com.dlsu.medflow.model.VisitStatus;
import com.dlsu.medflow.service.HospitalDataStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class DoctorDashboard {

    private final HospitalDataStore store;
    private final StatusTrackerView statusTrackerView;

    public DoctorDashboard(
            HospitalDataStore store,
            StatusTrackerView statusTrackerView) {

        this.store = store;
        this.statusTrackerView = statusTrackerView;
    }

    @GetMapping("/doctor")
    public String showDashboard(Model model) {

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

    @PostMapping("/doctor/visit/begin")
    public String beginAssessment(
            @RequestParam String visitId) {

        Visit visit = store.getAllVisits().stream()
                .filter(v -> v.getVisitId().equals(visitId))
                .findFirst()
                .orElse(null);

        if (visit != null
                && visit.getStatus()
                == VisitStatus.ASSIGNED_TO_DOCTOR) {

            Doctor doctor = visit.getAssignedDoctor();

            doctor.updateStatus(
                    visit,
                    VisitStatus.UNDER_DOCTOR_ASSESSMENT
            );

            store.save();
        }

        return "redirect:/doctor/visit?visitId=" + visitId;
    }

    @PostMapping("/doctor/visit/notes")
    public String saveClinicalNotes(
            @RequestParam String visitId,
            @RequestParam String notes) {

        Visit visit = store.getAllVisits().stream()
                .filter(v -> v.getVisitId().equals(visitId))
                .findFirst()
                .orElse(null);

        if (visit != null) {

            Doctor doctor = visit.getAssignedDoctor();

            visit.getMedicalRecord().setDoctorNotes(
                    doctor,
                    notes.trim()
            );

            store.save();
        }

        return "redirect:/doctor/visit?visitId=" + visitId;
    }

    @PostMapping("/doctor/visit/lab/single")
    public String addSingleLabTest(
            @RequestParam String visitId,
            @RequestParam String testName,
            RedirectAttributes redirectAttributes) {

        Visit visit = store.getAllVisits().stream()
                .filter(v -> v.getVisitId().equals(visitId))
                .findFirst()
                .orElse(null);

        String value = testName.trim();

        if (visit == null || value.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "labMessage",
                    "Enter a test name first."
            );

            return "redirect:/doctor/visit?visitId=" + visitId;
        }

        Doctor doctor = visit.getAssignedDoctor();

        // UNDERSTAND:
        // This replaces the JavaFX Add Single Test button action.
        doctor.createRequest(
                visit,
                value
        );

        store.save();

        redirectAttributes.addFlashAttribute(
                "labMessage",
                "Added and routed automatically."
        );

        return "redirect:/doctor/visit?visitId=" + visitId;
    }

    @PostMapping("/doctor/visit/lab/batch")
    public String addBatchLabTests(
            @RequestParam String visitId,
            @RequestParam String testNames,
            @RequestParam Priority priority,
            RedirectAttributes redirectAttributes) {

        Visit visit = store.getAllVisits().stream()
                .filter(v -> v.getVisitId().equals(visitId))
                .findFirst()
                .orElse(null);

        String raw = testNames.trim();

        if (visit == null || raw.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "labMessage",
                    "Enter at least one test name."
            );

            return "redirect:/doctor/visit?visitId=" + visitId;
        }

        String[] tests = raw.split(",");

        Doctor doctor = visit.getAssignedDoctor();

        // UNDERSTAND:
        // This replaces the JavaFX Add Batch button action.
        // The priority is passed to the existing overloaded
        // createRequest method as a String.
        doctor.createRequest(
                visit,
                tests,
                priority.name()
        );

        store.save();

        redirectAttributes.addFlashAttribute(
                "labMessage",
                "Batch added and routed automatically."
        );

        return "redirect:/doctor/visit?visitId=" + visitId;
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