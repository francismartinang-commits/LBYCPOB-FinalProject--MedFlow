# PROJECT TITLE
MedFlow: Hospital and Laboratory Information System

## TEAM MEMBERS
Paragas, Nash Breann C. - Nash Paragas

Ang, Francis Martin B. - Tin Ang

Balingit, Joshua Andrei B. - Jandrei-cpe

### PROBLEM STATEMENT & GOALS
To design, develop, and implement an Object-Oriented Hospital and Laboratory Information System that organizes patient care, hospital visits, laboratory workflows, and role-based access into one connected web application.

- To centralize patient, doctor, nurse, laboratory staff, and administrator information
- To automate the transition from patient registration to doctor recommendation and assignment
- To enforce role-based access control for Patients, Doctors, Nurses/Staff, Laboratory Staff, and Administrators
- To provide organized access to clinical notes, laboratory requests, findings, and released results
- To track the current status and history of each patient visit
- To automate intelligent laboratory request routing
- To allow doctors to create single or multiple laboratory requests
- To allow laboratory staff to encode findings based on their assigned laboratory section
- To provide a browser-based system using Spring Boot, Spring MVC, and Thymeleaf
- To maintain an organized and traceable hospital workflow from registration until the release of results

### TARGET USERS
The target users and beneficiaries of MedFlow are the people involved in the hospital workflow:

- Patients - Register visits, track their current visit status, and view released results
- Doctors - Assess patients, write clinical notes, create laboratory requests, review findings, and release results
- Laboratory Staff - Receive laboratory requests routed to their assigned section and encode findings
- Nurses / Staff - Handle doctor assignment, sample collection, laboratory hand-off, and walk-in patient registration
- Administrators - Manage user accounts, doctor categories, laboratory sections, and monitor the overall hospital workflow

### BRIEF DESCRIPTION
MedFlow is a web-based Hospital and Laboratory Information System designed to organize patient information, doctor assessments, laboratory requests, laboratory findings, and visit tracking from start to finish.

The system guides each patient through a clear 10-stage hospital workflow beginning from registration and ending with the release of results. Each hospital role is provided with its own dashboard and functions depending on its responsibilities.

MedFlow was originally developed as a JavaFX desktop application and was later converted into a Spring Boot Maven web application using Spring MVC and Thymeleaf while preserving the original OOP structure and hospital workflow.

- Purpose: To make hospital workflows more organized, traceable, and easier to manage
- Key Actions: Patient registration, doctor recommendation, laboratory request creation, automatic laboratory routing, findings submission, visit status tracking, and result release
- Target Users: Patients, Doctors, Nurses/Staff, Laboratory Staff, and Administrators
- Live Site: https://lbycpob-finalproject-medflow.onrender.com
- Repository: https://github.com/francismartinang-commits/LBYCPOB-FinalProject--MedFlow

### CORE OOP CONCEPTS

#### Abstraction
Abstraction is used through the abstract `User` class, which contains the common information and behavior shared by all hospital users.

The `User` class defines common attributes such as:

- User ID
- Name
- Username
- Password
- Role
- Account status

Each specific user role then provides its own implementation of the required behaviors.

Examples include:

- Patient
- Doctor
- Nurse
- LabStaff
- Admin

Some of the abstract methods used in the system include:

- `getDashboardView()`
- `buildDashboardModel()`
- `updateStatus()`

The project also uses service classes such as `DoctorRecommendationEngine` and `LabRoutingEngine` to hide the internal logic used for doctor recommendation and laboratory routing.

#### Encapsulation
Encapsulation is used to protect important patient, account, medical, and workflow information by keeping fields private and controlling access through methods.

Applications include:

- Patient - personal information and visit history
- MedicalRecord - doctor's notes and diagnosis
- Visit - current status, patient, assigned doctor, laboratory requests, and status history
- LabRequest - test name, priority, assigned laboratory section, findings, and findings status
- User - username, password, role, and active account status

The `Visit` status cannot be directly changed using a normal setter. Status changes are handled through validated workflow methods.

The password field is also kept private and is checked through methods instead of being directly exposed.

The `MedicalRecord` class also controls access to diagnosis and clinical notes depending on the role of the user requesting the information.

#### Inheritance
Inheritance is used to build the hierarchy of hospital users.

The abstract `User` class acts as the parent class while the specific hospital roles inherit its shared attributes and methods.

- User - Parent/Base class
- Patient - Child class
- Doctor - Child class
- Nurse - Child class
- LabStaff - Child class
- Admin - Child class

Each child class inherits the common user information while adding its own role-specific fields and behavior.

Examples:

- Doctor adds specialization and laboratory request creation
- LabStaff adds laboratory section
- Patient adds age, gender, contact number, address, and visit history
- Nurse and Admin specialize mainly through their role-specific behavior

#### Polymorphism
Polymorphism is used through method overriding and method overloading.

Possible polymorphic actions and behaviors include:

- `getDashboardView()` - Method Overriding
- `buildDashboardModel()` - Method Overriding
- `updateStatus()` - Method Overriding
- `createRequest()` - Method Overloading

##### Method Overriding
Each hospital role provides its own version of methods such as `getDashboardView()`, `buildDashboardModel()`, and `updateStatus()`.

For example:

- Doctor - displays assigned patients, assessments, and laboratory request functions
- Laboratory Staff - displays pending laboratory requests for the staff member's assigned section
- Patient - displays visit history and current visit status
- Nurse - displays registration, sample collection, and laboratory hand-off queues
- Admin - displays system statistics and management functions

##### Method Overloading
The Doctor class uses two versions of `createRequest()`:

- `createRequest(Visit visit, String testName)` - Creates a single routine laboratory request
- `createRequest(Visit visit, String[] testNames, String priority)` - Creates multiple laboratory requests with a selected priority such as Routine, Urgent, or STAT

### MAIN CLASSES

- User - Abstract class containing shared user information and behavior
- Patient - Stores patient information and visit history
- Doctor - Handles patient assessment, laboratory requests, findings review, and result release
- Nurse - Handles doctor assignment, sample collection, and laboratory hand-off
- LabStaff - Handles laboratory requests assigned to a specific laboratory section
- Admin - Handles user accounts, doctor categories, and laboratory sections
- Visit - Tracks one patient's hospital visit and its current status
- LabRequest - Stores laboratory test information, priority, routing, and findings
- MedicalRecord - Stores clinical notes and diagnosis
- Role - Defines the available hospital user roles
- Priority - Defines Routine, Urgent, and STAT laboratory request priorities
- VisitStatus - Defines the different stages of the hospital workflow
- HospitalDataStore - Stores users, visits, categories, and laboratory sections
- DoctorRecommendationEngine - Recommends a doctor category based on the patient's reason for visit
- LabRoutingEngine - Routes laboratory requests to the correct laboratory section

### USER STORIES

- As a Patient, I want to register a hospital visit and track its current status so that I know what stage of the hospital process I am in
- As a Doctor, I want to create laboratory requests directly from my dashboard so that the required tests can be processed for the patient
- As a Nurse, I want to confirm doctor assignment and sample collection so that the patient can continue through the hospital workflow
- As a Laboratory Staff member, I want to view requests assigned to my section and encode findings so that the doctor can review them
- As an Administrator, I want to manage hospital accounts and laboratory information so that the system remains organized

### CORE FEATURES

- Role-Based Access Control: A login system that provides different dashboards and functions depending on whether the user is a Patient, Doctor, Nurse/Staff, Laboratory Staff, or Administrator
- Patient Visit Lifecycle Tracker: A step-by-step workflow that tracks the patient's progress from registration until results are released
- Doctor Recommendation: Suggests an appropriate doctor category based on the patient's reason for visit
- Laboratory Request Creation: Allows doctors to create single or batch laboratory requests with different priorities
- Intelligent Laboratory Request Routing: Automatically sends each laboratory request to the appropriate laboratory section
- Laboratory Findings Encoding: Allows laboratory staff to encode findings for requests assigned to their section
- Status History Tracking: Stores the different stages a visit passes through together with timestamps
- Administrative Management: Allows administrators to create accounts, activate or deactivate users, and manage doctor categories and laboratory sections
- Walk-In Registration: Allows nurses to register patients directly through the system
- Doctor Findings Review: Allows doctors to review laboratory findings, enter a diagnosis, and release results to the patient

### HOSPITAL VISIT WORKFLOW

1. REGISTERED
2. ASSIGNED_TO_DOCTOR
3. UNDER_DOCTOR_ASSESSMENT
4. LABORATORY_REQUESTED
5. SAMPLE_COLLECTED
6. SENT_TO_LABORATORY
7. UNDER_LABORATORY_ANALYSIS
8. FINDINGS_SENT_TO_DOCTOR
9. DOCTOR_REVIEWED
10. RELEASED_TO_PATIENT

Each role can only perform the status transitions that belong to its responsibility.

- Nurse / Staff - Handles doctor assignment, sample collection, and sending samples to the laboratory
- Doctor - Handles assessment, laboratory request creation, findings review, and result release
- Laboratory Staff - Handles laboratory findings and sends completed findings back to the doctor
- Patient - Tracks the visit and views released information
- Administrator - Manages the system and user accounts

### LABORATORY REQUEST ROUTING

The `LabRoutingEngine` automatically routes laboratory requests to the proper laboratory section depending on the test name.

Examples:

- CBC - Hematology
- Urinalysis - Clinical Microscopy
- X-ray - Radiology / Imaging
- Creatinine - Chemistry / Biochemistry

Laboratory Staff only see requests that belong to their assigned laboratory section and are currently waiting for findings.

### APPLICATION ARCHITECTURE

MedFlow follows a Spring Boot web application structure.

The application is divided into different layers:

- Presentation Layer - Thymeleaf HTML templates and CSS
- Controller Layer - Spring MVC Controllers
- Business Logic - User hierarchy, Visit, MedicalRecord, LabRequest, DoctorRecommendationEngine, and LabRoutingEngine
- Data Management - HospitalDataStore
- Build Tool - Maven
- Deployment - Docker and Render

### CONTROLLERS

The main Spring MVC controllers used in the project are:

- AuthController - Handles login, logout, and patient registration
- DashboardController - Handles the shared dashboard route for all user roles
- PatientController - Handles new visits and patient timelines
- DoctorController - Handles doctor assessment, notes, laboratory requests, findings review, and result release
- NurseController - Handles walk-in registration, doctor assignment, sample collection, and laboratory hand-off
- LabStaffController - Handles laboratory findings submission
- AdminController - Handles account creation, account activation, doctor categories, and laboratory sections

### DEVELOPMENT HISTORY

MedFlow originally started as a JavaFX desktop application.

During development, the project was converted into a Spring Boot Maven web application. The original object-oriented models and hospital workflow were preserved while the JavaFX dashboards and interface components were replaced with Spring MVC controllers and Thymeleaf web pages.

The conversion allowed the application to be accessed through a normal web browser while keeping the original OOP structure of the system.

Spring Initializr was used to generate and configure the Spring Boot project during the conversion.

### TECHNOLOGIES USED

#### Programming Language
- Java

#### Framework
- Spring Boot
- Spring MVC

#### Frontend
- Thymeleaf
- HTML
- CSS

#### Build and Development
- IntelliJ IDEA

#### Deployment
- Docker
- Render

### LIVE APPLICATION

Live Site:

https://lbycpob-finalproject-medflow.onrender.com

GitHub Repository:

https://github.com/francismartinang-commits/LBYCPOB-FinalProject--MedFlow

### REFERENCES AND RESOURCES

- Java Documentation
- Spring Boot Documentation
- Spring Initializr
- IntelliJ IDEA
- GitHub
- Render
- Bro Code Java Programming Resources
