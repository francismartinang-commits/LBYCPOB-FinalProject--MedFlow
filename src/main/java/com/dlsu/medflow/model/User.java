package com.dlsu.medflow.model;

import com.dlsu.medflow.service.HospitalDataStore;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * ABSTRACTION: {@code User} is the general parent class identified in the
 * proposal ("User/Patient - name, ID"). It only exposes what every account
 * in the system has in common (identity + credentials + role) and leaves the
 * "how do I show my dashboard" and "how do I move a visit forward" questions
 * to be answered differently by every concrete role.
 *
 * <p>Converted from the JavaFX edition: {@code displayDashboard(...)} used to
 * build and return a JavaFX {@code Parent} directly. In this Spring MVC /
 * Thymeleaf edition, a role can no longer hand back a UI node — the view is
 * rendered server-side from an HTML template. So the same idea now takes two
 * smaller abstract methods instead of one:</p>
 * <ul>
 *   <li>{@link #getDashboardView()} — which Thymeleaf template represents
 *       "my dashboard" (was: which JavaFX node)</li>
 *   <li>{@link #buildDashboardModel(HospitalDataStore)} — which data that
 *       template needs (was: data the JavaFX node pulled from the store
 *       itself while building its widgets)</li>
 * </ul>
 * <p>Both are still resolved polymorphically by a single, role-agnostic
 * {@code GET /dashboard} controller method — see
 * {@code com.dlsu.medflow.web.DashboardController}.</p>
 *
 * <p>ENCAPSULATION: every field is private. The password is never exposed
 * through a getter at all - only {@link #checkPassword(String)} can compare
 * against it, and even {@code setPassword} enforces a minimum length so a
 * caller cannot silently corrupt an account with a blank password.</p>
 */
// UNDERSTAND: User is the abstract root entity representing generic user credentials, profile information, and role polymorphism.
// DECISION: Implement Serializable to ensure all user domain subclasses support distributed caching and state persistence.
public abstract class User implements Serializable {

    // UNDERSTAND: Core identity, security credentials, role, and active status state fields must be encapsulated.
    // DECISION: Declare immutable identity/role fields as final and keep mutable credentials private with sensible defaults.
    private final String userId;
    private String name;
    private String username;
    private String password;
    private final Role role;
    private boolean active = true;

    // UNDERSTAND: Subclass construction requires mandatory core identity and credential values.
    // DECISION: Validate non-null inputs via Objects.requireNonNull in the protected constructor to enforce invariant integrity.
    protected User(String userId, String name, String username, String password, Role role) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.name = Objects.requireNonNull(name, "name");
        this.username = Objects.requireNonNull(username, "username");
        this.password = Objects.requireNonNull(password, "password");
        this.role = Objects.requireNonNull(role, "role");
    }
}