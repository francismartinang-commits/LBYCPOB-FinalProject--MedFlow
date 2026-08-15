package com.dlsu.medflow.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

/**
 * INHERITANCE: child class of {@link User}. "Patient - can register and view
 * released results" (System Framework / Scope).
 */
@Entity
public class Patient extends User {

    private int age;
    private String gender;
    private String contactNumber;
    private String address;

    @OneToMany(
            mappedBy = "patient",
            cascade = CascadeType.ALL
    )
    private List<Visit> visitHistory = new ArrayList<>();

    // UNDERSTAND:
    // JPA requires a no-argument constructor when loading
    // patient accounts from the database.
    protected Patient() {
    }

    public Patient(String userId, String name, String username, String password,
                   int age, String gender, String contactNumber, String address) {

        super(userId, name, username, password, Role.PATIENT);
        this.age = age;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.address = address;
    }

    @Override
    public void updateStatus(Visit visit, VisitStatus newStatus) {
        // A patient is a passive participant in the workflow; they can view
        // progress but never move it forward themselves. Distinct behaviour
        // from every other role is itself an example of method overriding.
        throw new SecurityException("Patients cannot change a visit's status.");
    }

    public void addVisit(Visit visit) {
        visitHistory.add(visit);
    }

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