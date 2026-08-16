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
            return true;
         }
      }
      return false;
   }


    // ---------------------------------------------------------------------
    // Registration
    // ---------------------------------------------------------------------

    public Patient registerPatient(String name, int age, String gender, String contactNumber,
                                   String address, String username, String password) {
        Patient patient = new Patient(nextUserId("PT"), name, username, password, age, gender, contactNumber, address);
        users.add(patient);
        return patient;
    }


    /**
     * Creates a new visit and immediately runs the Doctor Recommendation
     * engine against the reason for visit, exactly as described in the
     * System Framework ("Reason-for-visit input -> Doctor recommendation").
     * The recommendation is only a suggestion until a Nurse/Staff confirms it.
     */
    public Visit registerVisit(Patient patient, String reasonForVisit) {
        Visit visit = new Visit(nextVisitId(), patient, reasonForVisit);
        String category = DoctorRecommendationEngine.recommendCategory(reasonForVisit);
        Doctor recommended = findAvailableDoctorForCategory(category);
        visit.setRecommendedDoctor(recommended);
        patient.addVisit(visit);
        visits.add(visit);
        return visit;
    }

    private Doctor findAvailableDoctorForCategory(String category) {
        Doctor fallback = null;
        for (User user : users) {
            if (user instanceof Doctor && user.isActive()) {
                Doctor doctor = (Doctor) user;
                if (fallback == null) {
                    fallback = doctor;
                }
                if (doctor.getSpecialization().equalsIgnoreCase(category)) {
                    return doctor;
                }
            }
        }
        return fallback;
    }

}




