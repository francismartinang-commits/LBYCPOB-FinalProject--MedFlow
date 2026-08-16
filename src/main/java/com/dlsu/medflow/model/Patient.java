package com.dlsu.medflow.model;

import com.dlsu.medflow.service.HospitalDataStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * INHERITANCE: child class of {@link User}. "Patient - can register and view
 * released results" (System Framework / Scope).
 */
public class Patient extends User {

    private int age;
    private String gender;
    private String contactNumber;
    private String address;
    private final List<Visit> visitHistory = new ArrayList<>();

    // UNDERSTAND: Instantiating a Patient requires demographic details alongside core credentials.
    // DECISION: Pass identity fields to super constructor with Role.PATIENT and assign demographic properties directly.
    public Patient(String userId, String name, String username, String password,
                   int age, String gender, String contactNumber, String address) {
        super(userId, name, username, password, Role.PATIENT);
        this.age = age;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.address = address;
    }

    // UNDERSTAND: Framework routing maps Patient users to their dedicated web template.
    // DECISION: Return explicit relative view path pointing directly to the patient dashboard view.
    @Override
    public String getDashboardView() {
        return "patient/dashboard";
    }
}