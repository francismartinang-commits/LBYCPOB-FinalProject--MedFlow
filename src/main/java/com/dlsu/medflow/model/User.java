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

    // ---- shared behaviour -------------------------------------------------

    /** Shared method mentioned under Inheritance ("shared methods login(), logout()"). */
    // UNDERSTAND: Password verification must perform safe null-safe comparisons without exposing raw credentials.
    // DECISION: Implement checkPassword returning a boolean comparison result rather than providing a raw password getter.
    public boolean checkPassword(String attempt) {
        return password != null && password.equals(attempt);
    }

    // UNDERSTAND: Password updates must enforce length boundaries to prevent weak or corrupted credentials.
    // DECISION: Validate non-null input with a minimum 4-character length check, throwing IllegalArgumentException on failure.
    public void setPassword(String newPassword) {
        if (newPassword == null || newPassword.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters long.");
        }
        this.password = newPassword;
    }

    /** Up to two initials for the sidebar avatar circle — shared by every role's dashboard layout. */
    // UNDERSTAND: Avatar components require a two-letter uppercase representation derived from the user's name.
    // DECISION: Tokenize user name by whitespace and extract up to two leading characters, defaulting to "?" if empty.
    public String getInitials() {
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                sb.append(Character.toUpperCase(part.charAt(0)));
            }
            if (sb.length() >= 2) {
                break;
            }
        }
        return sb.length() > 0 ? sb.toString() : "?";
    }

    /** "Doctor - General Medicine" / "Laboratory Staff - Hematology" / just the role name for everyone else. */
    // UNDERSTAND: UI headers require display strings describing the user's operational role.
    // DECISION: Delegate getRoleDetail directly to role.getDisplayName() for default string formatting.
    public String getRoleDetail() {
        return role.getDisplayName();
    }

    // ---- POLYMORPHISM ------------------------------------------------------

    /**
     * METHOD OVERRIDING: every subclass names a completely different
     * Thymeleaf template here — a Doctor's dashboard template is nothing
     * like a Laboratory Staff member's queue template — exactly as the
     * JavaFX edition returned a different {@code Parent} per role.
     */
    // UNDERSTAND: Polymorphic view resolution requires role subclasses to define their specific Thymeleaf template key.
    // DECISION: Declare abstract getDashboardView method to force implementation in concrete user subclasses.
    public abstract String getDashboardView();

    /**
     * METHOD OVERRIDING: every subclass gathers completely different data
     * for that template — assigned patients for a Doctor, this section's
     * pending queue for a LabStaff, and so on. Returned as a plain
     * {@code Map} (not a Spring {@code Model}) so the domain layer still
     * doesn't need to import anything from the web framework.
     */
    // UNDERSTAND: Dashboard rendering requires role-specific model data without coupling the domain model to Spring MVC APIs.
    // DECISION: Declare abstract buildDashboardModel returning a raw Map<String, Object> populated using HospitalDataStore.
    public abstract Map<String, Object> buildDashboardModel(HospitalDataStore store);

    /**
     * METHOD OVERRIDING: this is the single "validated method" through which
     * {@link Visit#getStatus()} may ever change (see the Encapsulation note
     * on {@code Visit}). Each role only allows the specific transitions it is
     * responsible for in the System Framework; anyone else attempting an
     * out-of-turn transition receives an {@link IllegalStateException}.
     */
    // UNDERSTAND: Visit status state transitions must follow strict role-based workflow permissions.
    // DECISION: Declare abstract updateStatus method to mandate custom transition rule validation in each subclass.
    public abstract void updateStatus(Visit visit, VisitStatus newStatus);

    // ---- getters / setters --------------------------------------------------

    // UNDERSTAND: Encapsulated properties require controlled getter and setter access points.
    // DECISION: Provide getters for userId, name, username, role, and active status with non-null checks on setters.
    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = Objects.requireNonNull(username, "username");
    }

    public Role getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // UNDERSTAND: Diagnostic logs and UI dropdown lists require clear human-readable string representations of user instances.
    // DECISION: Override toString to format name alongside the role display name.
    @Override
    public String toString() {
        return name + " (" + role.getDisplayName() + ")";
    }
}