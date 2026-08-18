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
