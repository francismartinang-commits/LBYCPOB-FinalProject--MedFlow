package com.dlsu.medflow.model;

import com.dlsu.medflow.service.HospitalDataStore;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * INHERITANCE: child class of {@link User}. "Admin - can manage user
 * accounts, doctor categories, and laboratory sections" (System Framework).
 */
public class Admin extends User {

    // UNDERSTAND: Admin requires a concrete sub-class implementation of User with explicit Role assignment.
    // DECISION: Pass Role.ADMIN directly to super constructor to enforce fixed role classification.
    public Admin(String userId, String name, String username, String password) {
        super(userId, name, username, password, Role.ADMIN);
    }