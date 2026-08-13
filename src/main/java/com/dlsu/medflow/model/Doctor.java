package com.dlsu.medflow.model;

import com.dlsu.medflow.gui.doctor.DoctorDashboard;
import com.dlsu.medflow.service.HospitalDataStore;
import com.dlsu.medflow.service.LabRoutingEngine;
import javafx.scene.Parent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Doctor extends User {

    private String specialization;

    public Doctor(String userId, String name, String username, String password, String specialization) {
        super(userId, name, username, password, Role.DOCTOR);
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public Parent displayDashboard(HospitalDataStore store) {
        return new DoctorDashboard(this, store);
    }
