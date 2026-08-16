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
    // ---------------------------------------------------------------------
    // Queues used by the different dashboards
    // ---------------------------------------------------------------------

    public List<Visit> getPendingRegistrations() {
        return filterByStatus(VisitStatus.REGISTERED);
    }

    public List<Visit> getPendingSampleCollection() {
        return filterByStatus(VisitStatus.LABORATORY_REQUESTED);
    }

    public List<Visit> getPendingSendToLaboratory() {
        return filterByStatus(VisitStatus.SAMPLE_COLLECTED);
    }

    private List<Visit> filterByStatus(VisitStatus status) {
        List<Visit> result = new ArrayList<>();
        for (Visit visit : visits) {
            if (visit.getStatus() == status) {
                result.add(visit);
            }
        }
        return result;
    }

    public List<Visit> getVisitsForDoctor(Doctor doctor) {
        List<Visit> result = new ArrayList<>();
        for (Visit visit : visits) {
            if (visit.getAssignedDoctor() == doctor) {
                result.add(visit);
            }
        }
        return result;
    }
    /** Lab requests still awaiting findings, routed to the given section, paired with their parent visit. */
    public List<LabQueueItem> getPendingLabRequests(String section) {
        List<LabQueueItem> result = new ArrayList<>();
        for (Visit visit : visits) {
            if (visit.getStatus() != VisitStatus.UNDER_LABORATORY_ANALYSIS) {
                continue;
            }
            for (LabRequest request : visit.getLabRequests()) {
                if (!request.isFindingsEncoded() && request.getAssignedSection().equals(section)) {
                    result.add(new LabQueueItem(visit, request));
                }
            }
        }
        return result;
    }
    // ---------------------------------------------------------------------
    // Admin-facing lists
    // ---------------------------------------------------------------------

    public List<User> getAllUsers() {
        return Collections.unmodifiableList(users);
    }

    public List<Doctor> getAllDoctors() {
        List<Doctor> result = new ArrayList<>();
        for (User user : users) {
            if (user instanceof Doctor) {
                result.add((Doctor) user);
            }
        }
        return result;
    }



    public List<Patient> getAllPatients() {
        List<Patient> result = new ArrayList<>();
        for (User user : users) {
            if (user instanceof Patient) {
                result.add((Patient) user);
            }
        }
        return result;
    }

    public List<Visit> getAllVisits() {
        return Collections.unmodifiableList(visits);
    }

    public List<String> getDoctorCategories() {
        return Collections.unmodifiableList(doctorCategories);
    }

    public void addDoctorCategory(String category) {
        if (category != null && !category.isBlank() && !doctorCategories.contains(category)) {
            doctorCategories.add(category);
        }
    }

    public void removeDoctorCategory(String category) {
        doctorCategories.remove(category);
    }

    public List<String> getLabSections() {
        return Collections.unmodifiableList(labSections);
    }

    public void addLabSection(String section) {
        if (section != null && !section.isBlank() && !labSections.contains(section)) {
            labSections.add(section);
        }
    }

    public void removeLabSection(String section) {
        labSections.remove(section);
    }

    public void addUser(User user) {
        users.add(user);
    }

    public String generateUserId(String prefix) {
        return nextUserId(prefix);
    }

    private String nextUserId(String prefix) {
        userSequence++;
        return prefix + "-" + String.format("%04d", userSequence);
    }

    private String nextVisitId() {
        visitSequence++;
        return "V-" + String.format("%05d", visitSequence);
    }

    // ---------------------------------------------------------------------
    // Demo data
    // ---------------------------------------------------------------------

    private void seedDemoData() {
        doctorCategories.add("General Medicine");
        doctorCategories.add("Cardiology");
        doctorCategories.add("Pediatrics");
        doctorCategories.add("Dermatology");
        doctorCategories.add("Orthopedics");
        doctorCategories.add("Neurology");
        doctorCategories.add("Gastroenterology");
        doctorCategories.add("Obstetrics & Gynecology");
        doctorCategories.add("Pulmonology");
        doctorCategories.add("ENT (Otorhinolaryngology)");

        labSections.add("Hematology");
        labSections.add("Clinical Microscopy");
        labSections.add("Chemistry / Biochemistry");
        labSections.add("Radiology / Imaging");
        labSections.add("Cardiac Diagnostics");
        labSections.add("Microbiology");
        labSections.add("General Laboratory");

        Admin admin = new Admin(nextUserId("AD"), "System Administrator", "admin", "admin123");
        users.add(admin);

        Doctor drReyes = new Doctor(nextUserId("DR"), "Dr. Ana Reyes", "dr.reyes", "doctor123", "General Medicine");
        Doctor drSantos = new Doctor(nextUserId("DR"), "Dr. Miguel Santos", "dr.santos", "doctor123", "Cardiology");
        Doctor drCruz = new Doctor(nextUserId("DR"), "Dr. Bea Cruz", "dr.cruz", "doctor123", "Pediatrics");
        Doctor drTan = new Doctor(nextUserId("DR"), "Dr. Carlos Tan", "dr.tan", "doctor123", "Orthopedics");
        users.add(drReyes);
        users.add(drSantos);
        users.add(drCruz);
        users.add(drTan);

        Nurse nurseRamos = new Nurse(nextUserId("NS"), "Liza Ramos", "nurse.ramos", "nurse123");
        users.add(nurseRamos);

        LabStaff labDizon = new LabStaff(nextUserId("LB"), "Jun Dizon", "lab.dizon", "lab123", "Hematology");
        LabStaff labLim = new LabStaff(nextUserId("LB"), "Grace Lim", "lab.lim", "lab123", "Clinical Microscopy");
        LabStaff labPaolo = new LabStaff(nextUserId("LB"), "Paolo Reyes", "lab.paolo", "lab123", "Radiology / Imaging");
        LabStaff labManalo = new LabStaff(nextUserId("LB"), "Ella Manalo", "lab.manalo", "lab123", "Chemistry / Biochemistry");
        users.add(labDizon);
        users.add(labLim);
        users.add(labPaolo);
        users.add(labManalo);

}




