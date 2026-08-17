package com.dlsu.medflow.web;

import com.dlsu.medflow.service.HospitalDataStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/** Replaces the "Submit Findings" button in {@code LabStaffDashboard}. */
@Controller
@RequestMapping("/labstaff")
public class LabStaffController {

    private final HospitalDataStore store;

    public LabStaffController(HospitalDataStore store) {
        this.store = store;
    }
}