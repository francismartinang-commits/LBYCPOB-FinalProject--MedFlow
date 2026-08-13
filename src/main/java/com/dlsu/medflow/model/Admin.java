package com.dlsu.medflow.model;

import com.dlsu.medflow.gui.admin.AdminDashboard;
import com.dlsu.medflow.service.HospitalDataStore;
import javafx.scene.Parent;

public class Admin extends User {

    public Admin(String userId, String name, String username, String password) {
        super(userId, name, username, password, Role.ADMIN);
    }

    @Override
    public Parent displayDashboard(HospitalDataStore store) {
        return new AdminDashboard(this, store);
    }

    @Override
    public void updateStatus(Visit visit, VisitStatus newStatus) {
        // Unlike every other role, an Admin may correct a visit to ANY stage.
        // This is a deliberate, distinct override: administrative privilege
        // means no workflow restriction applies, only an audit trail entry.
        visit.advance(this, newStatus);
    }
}
