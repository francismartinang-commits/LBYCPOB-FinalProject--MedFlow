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

    // UNDERSTAND: Patient dashboard needs to display visit history in reverse chronological order.
    // DECISION: Copy visit history list, apply Collections.reverse to put most recent visits first, and expose in model map.
    @Override
    public Map<String, Object> buildDashboardModel(HospitalDataStore store) {
        List<Visit> visits = new ArrayList<>(visitHistory);
        java.util.Collections.reverse(visits); // most recent first, same order the JavaFX list used
        Map<String, Object> model = new HashMap<>();
        model.put("visits", visits);
        return model;
    }

    // UNDERSTAND: Patients are passive participants who can view status but cannot modify workflow state.
    // DECISION: Override updateStatus to unconditionally throw SecurityException, preventing status modification.
    @Override
    public void updateStatus(Visit visit, VisitStatus newStatus) {
        // A patient is a passive participant in the workflow; they can view
        // progress but never move it forward themselves. Distinct behaviour
        // from every other role is itself an example of method overriding.
        throw new SecurityException("Patients cannot change a visit's status.");
    }

    // UNDERSTAND: External registration and visit logging services need to associate new visits with the patient.
    // DECISION: Provide helper method to append visits to the internal visitHistory list.
    public void addVisit(Visit visit) {
        visitHistory.add(visit);
    }

    // UNDERSTAND: Patient data accessors are required for profile rendering and demographic management.
    // DECISION: Expose standard getters and setters for visit history, age, gender, contact number, and address fields.
    public List<Visit> getVisitHistory() {
        return visitHistory;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
