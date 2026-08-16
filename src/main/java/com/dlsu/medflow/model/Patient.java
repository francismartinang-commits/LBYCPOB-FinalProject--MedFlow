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
}