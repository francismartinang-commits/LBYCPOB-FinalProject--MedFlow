package com.dlsu.medflow.web;

import com.dlsu.medflow.service.HospitalDataStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.dlsu.medflow.model.LabRequest;
import com.dlsu.medflow.model.LabStaff;
import com.dlsu.medflow.model.Visit;
import com.dlsu.medflow.model.VisitStatus;
import com.dlsu.medflow.web.support.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Replaces the "Submit Findings" button in {@code LabStaffDashboard}. */
@Controller
@RequestMapping("/labstaff")
public class LabStaffController {

    private final HospitalDataStore store;

    public LabStaffController(HospitalDataStore store) {
        this.store = store;
    }

    @PostMapping("/requests/{visitId}/{requestId}/submit")
    public String submitFindings(@PathVariable String visitId, @PathVariable String requestId,
                                 @RequestParam String findings, HttpSession session) {
        LabStaff labStaff = (LabStaff) session.getAttribute(SessionKeys.CURRENT_USER);
        Visit visit = store.getVisitById(visitId);
        if (visit == null || findings == null || findings.isBlank()) {
            return "redirect:/dashboard";
        }
        for (LabRequest request : visit.getLabRequests()) {
            if (request.getRequestId().equals(requestId)) {
                request.encodeFindings(findings.trim());
                if (visit.allFindingsEncoded()) {
                    labStaff.updateStatus(visit, VisitStatus.FINDINGS_SENT_TO_DOCTOR);
                }
                store.save();
                break;
            }
        }
        return "redirect:/dashboard";
    }
}