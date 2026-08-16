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

   private final List<User> users = new ArrayList<>();
   private final List<Visit> visits = new ArrayList<>();
   private final List<String> doctorCategories = new ArrayList<>();
   private final List<String> labSections = new ArrayList<>();

   private int userSequence = 0;
   private int visitSequence = 0;

// ---------------------------------------------------------------------
// Loading / saving
// ---------------------------------------------------------------------


   public static HospitalDataStore loadOrCreate() {
      File file = new File(DATA_FILE);
      if (file.exists()) {
         try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object loaded = in.readObject();
            if (loaded instanceof HospitalDataStore) {
               return (HospitalDataStore) loaded;
            }
         } catch (IOException | ClassNotFoundException ex) {
            System.err.println("Could not read saved data, starting fresh: " + ex.getMessage());
         }
      }
      HospitalDataStore store = new HospitalDataStore();
      store.seedDemoData();
      store.save();
      return store;
   }
   public void save() {
      try {
         File dir = new File(DATA_DIR);
         if (!dir.exists()) {
            dir.mkdirs();
         }
         try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(this);
         }
      } catch (IOException ex) {
         System.err.println("Could not save data: " + ex.getMessage());
      }
   }




   // ---------------------------------------------------------------------
   // Authentication
   // ---------------------------------------------------------------------

   public User authenticate(String username, String password) {
      for (User user : users) {
         if (user.getUsername().equalsIgnoreCase(username) && user.isActive() && user.checkPassword(password)) {
            return user;
         }
      }
      return null;
   }



   public boolean usernameTaken(String username) {
      for (User user : users) {
         if (user.getUsername().equalsIgnoreCase(username)) {
}



