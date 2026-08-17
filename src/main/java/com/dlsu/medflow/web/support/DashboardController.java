package com.dlsu.medflow.web;

import com.dlsu.medflow.service.HospitalDataStore;
import org.springframework.stereotype.Controller;

/**
 * Replaces {@code MainShell}'s call to {@code user.displayDashboard(store)}.
 *
 * <p>POLYMORPHISM: this is the whole controller. One route, five roles.
 * It never asks "which role is this?" - it just calls two methods on the
 * logged-in {@link User} and lets that object's own overridden behaviour
 * decide both the template and the data, exactly as the JavaFX version let
 * each role decide its own {@code Parent}.</p>
 */
@Controller
public class DashboardController {

    private final HospitalDataStore store;

    public DashboardController(HospitalDataStore store) {
        this.store = store;
    }
}