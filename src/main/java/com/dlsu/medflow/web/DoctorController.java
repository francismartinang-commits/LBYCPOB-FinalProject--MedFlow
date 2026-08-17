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