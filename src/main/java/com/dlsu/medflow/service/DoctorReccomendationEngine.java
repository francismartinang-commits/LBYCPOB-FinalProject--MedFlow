package com.dlsu.medflow.service;

import java.util.LinkedHashMap;
import java.util.Map;



/**
 * "To automate transition from visitation and consultation to a targeted
 * Doctor Recommendation" (Objectives) / "Intelligent Lab Request Routing"
 * counterpart on the consultation side.
 *
 * <p>This performs the suggestion only - it does NOT diagnose (see
 * Limitations: "Doctor Recommendation only suggests an appropriate
 * doctor/department based on reported symptoms; it does not diagnose").
 * A simple keyword match is enough to demonstrate the automation described
 * in the proposal without pretending to be real clinical decision support.</p>
 */

public final class DoctorRecommendationEngine {

    static {
        CATEGORY_KEYWORDS.put("Cardiology",
                new String[]{"heart", "chest pain", "palpitation", "hypertension", "blood pressure"});
        CATEGORY_KEYWORDS.put("Pediatrics",
                new String[]{"child", "baby", "infant", "toddler", "kid"});
        CATEGORY_KEYWORDS.put("Dermatology",
                new String[]{"skin", "rash", "itch", "acne", "eczema"});
        CATEGORY_KEYWORDS.put("Orthopedics",
                new String[]{"bone", "fracture", "joint", "sprain", "back pain", "knee", "shoulder"});
        CATEGORY_KEYWORDS.put("Neurology",
                new String[]{"headache", "migraine", "dizziness", "seizure", "numbness"});
        CATEGORY_KEYWORDS.put("Gastroenterology",
                new String[]{"stomach", "abdominal", "nausea", "vomiting", "diarrhea", "ulcer"});
        CATEGORY_KEYWORDS.put("Obstetrics & Gynecology",
                new String[]{"pregnan", "menstrual", "prenatal", "gynec"});
        CATEGORY_KEYWORDS.put("Pulmonology",
                new String[]{"cough", "breath", "asthma", "wheeze", "lungs"});
        CATEGORY_KEYWORDS.put("ENT (Otorhinolaryngology)",
                new String[]{"ear", "nose", "throat", "sinus", "tonsil"});
    }
    public static final String DEFAULT_CATEGORY = "General Medicine";



    private DoctorRecommendationEngine() {
    }

    /** Returns the suggested doctor category for the given reason-for-visit text. */
    public static String recommendCategory(String reasonForVisit) {
        if (reasonForVisit == null || reasonForVisit.isBlank()) {
            return DEFAULT_CATEGORY;
        }
        String text = reasonForVisit.toLowerCase();
        for (Map.Entry<String, String[]> entry : CATEGORY_KEYWORDS.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (text.contains(keyword)) {
                    return entry.getKey();
                }
            }
        }
        return DEFAULT_CATEGORY;
    }
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


