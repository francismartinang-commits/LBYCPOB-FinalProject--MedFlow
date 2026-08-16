package com.dlsu.medflow.service;

import java.util.LinkedHashMap;
import java.util.Map;
/**
 * "To Automate Intelligent Laboratory Request Routing" (Objectives). Routes
 * a test name straight to the laboratory section that should process it, so
 * requests reach the right queue without manual sorting.
 */
public final class LabRoutingEngine {
    private static final Map<String, String[]> SECTION_KEYWORDS = new LinkedHashMap<>();

    static {
        SECTION_KEYWORDS.put("Hematology",
                new String[]{"cbc", "blood count", "hemoglobin", "platelet", "hematology"});
        SECTION_KEYWORDS.put("Clinical Microscopy",
                new String[]{"urinalysis", "urine", "fecalysis", "stool"});
        SECTION_KEYWORDS.put("Chemistry / Biochemistry",
                new String[]{"glucose", "cholesterol", "lipid", "creatinine", "liver function",
                        "kidney function", "electrolyte", "fbs", "hba1c"});
        SECTION_KEYWORDS.put("Radiology / Imaging",
                new String[]{"x-ray", "xray", "ct scan", "mri", "ultrasound", "imaging"});
        SECTION_KEYWORDS.put("Cardiac Diagnostics",
                new String[]{"ecg", "ekg", "echo", "cardiac", "troponin"});
        SECTION_KEYWORDS.put("Microbiology",
                new String[]{"culture", "sensitivity", "swab", "gram stain"});
    }
    public static final String DEFAULT_SECTION = "General Laboratory";

    private LabRoutingEngine() {
    }
}