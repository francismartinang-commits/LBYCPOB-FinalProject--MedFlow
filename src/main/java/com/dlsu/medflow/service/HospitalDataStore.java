package com.dlsu.medflow.service;

import com.dlsu.medflow.model.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Central in-memory data store for the whole system: every registered user,
 * every visit, and the admin-managed lookup lists (doctor categories and
 * laboratory sections) described in the Scope ("Admin-level management of
 * user accounts, doctor categories, and laboratory sections").
 *
 * <p>The store is also responsible for saving/loading itself to disk (Java
 * serialization) so demo data survives between runs, and for seeding a set
 * of ready-to-use demo accounts the first time the application launches.</p>

 */
 public class HospitalDataStore implements Serializable {
    private static final String DATA_DIR = System.getProperty("user.home") + File.separator + ".medflow";
    private static final String DATA_FILE = DATA_DIR + File.separator + "medflow_data.ser";
