## ADDED Requirements

### Requirement: Teacher can create courses with uniqueness scoped to the teacher
The system SHALL allow an authenticated teacher to create a course whose title is unique within that teacher's own course list.

#### Scenario: Successful course creation
- **WHEN** an authenticated teacher submits a non-empty course title that is not already used by that same teacher
- **THEN** the system creates the course and makes it available in the teacher dashboard

#### Scenario: Course creation rejected for duplicate title in the same teacher scope
- **WHEN** an authenticated teacher submits a course title that already exists in that teacher's own course list
- **THEN** the system MUST reject the creation and report the duplicate title conflict

#### Scenario: Course creation rejected for empty title
- **WHEN** an authenticated teacher submits an empty course title
- **THEN** the system MUST reject the creation and report the missing required field

### Requirement: Teacher can enroll students into a course with on-the-fly registration
The system SHALL allow an authenticated teacher to add students into a selected course by searching with student identification and creating the student if it does not already exist.

#### Scenario: Add a new student to a course
- **WHEN** an authenticated teacher enters a student identification that does not exist and provides the required student data `ID`, `full name`, and `email`
- **THEN** the system creates the student record and enrolls that student into the selected course

#### Scenario: Reject incomplete on-the-fly student creation
- **WHEN** an authenticated teacher enters a new student identification but omits any required field among `ID`, `full name`, or `email`
- **THEN** the system MUST reject the enrollment and MUST NOT create a partial student record

#### Scenario: Reuse existing student by identification
- **WHEN** an authenticated teacher enters a student identification that already exists in the local student directory
- **THEN** the system retrieves the existing student data and enrolls that student into the selected course without creating a duplicate global student record

### Requirement: Teacher can view enrolled students in the course detail
The system SHALL expose the enrolled students of a course within the course detail workflow used by the MVP.

#### Scenario: Course detail shows enrolled student list
- **WHEN** an authenticated teacher opens the detail view of a course with enrolled students
- **THEN** the system shows the student list needed to manage grades, program definition, and report generation
