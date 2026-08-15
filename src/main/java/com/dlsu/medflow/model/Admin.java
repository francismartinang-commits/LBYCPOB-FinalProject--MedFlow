package com.dlsu.medflow.model;

import jakarta.persistence.Entity;

/**
 * INHERITANCE:
 * Admin is a specialized type of User.
 */
@Entity
public class Admin extends User {

    // UNDERSTAND:
    // JPA requires a no-argument constructor when loading
    // Admin objects from the database.
    protected Admin() {
    }

    // UNDERSTAND:
    // The constructor sends the common account information to User
    // while automatically assigning the ADMIN role.
    public Admin(
            String userId,
            String name,
            String username,
            String password) {

        super(
                userId,
                name,
                username,
                password,
                Role.ADMIN
        );
    }

    // UNDERSTAND:
    // Admin overrides updateStatus because administrators are allowed
    // to correct a visit regardless of its current stage.
    @Override
    public void updateStatus(
            Visit visit,
            VisitStatus newStatus) {

        // DECISION:
        // Administrative users are not restricted by the normal
        // hospital workflow transitions.
        visit.advance(this, newStatus);
    }
}