package com.dlsu.medflow.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

import java.io.Serializable;
import java.util.Objects;

/**
 * ABSTRACTION: {@code User} is the general parent class identified in the
 * proposal ("User/Patient - name, ID"). It only exposes what every account
 * in the system has in common (identity + credentials + role) and leaves
 * each concrete role responsible for its own workflow behavior.
 *
 * <p>ENCAPSULATION: every field is private. The password is never exposed
 * through a getter at all - only {@link #checkPassword(String)} can compare
 * against it, and even {@code setPassword} enforces a minimum length so a
 * caller cannot silently corrupt an account with a blank password.</p>
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class User implements Serializable {

    @Id
    private String userId;

    private String name;
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean active = true;

    // UNDERSTAND:
    // JPA requires a no-argument constructor when loading
    // User subclasses from the database.
    protected User() {
    }

    protected User(String userId, String name, String username, String password, Role role) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.name = Objects.requireNonNull(name, "name");
        this.username = Objects.requireNonNull(username, "username");
        this.password = Objects.requireNonNull(password, "password");
        this.role = Objects.requireNonNull(role, "role");
    }

    // ---- shared behaviour -------------------------------------------------

    /** Shared method mentioned under Inheritance ("shared methods login(), logout()"). */
    public boolean checkPassword(String attempt) {
        return password != null && password.equals(attempt);
    }

    public void setPassword(String newPassword) {
        if (newPassword == null || newPassword.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters long.");
        }
        this.password = newPassword;
    }
}