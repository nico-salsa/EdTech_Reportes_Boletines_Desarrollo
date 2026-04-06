## ADDED Requirements

### Requirement: Teacher can record grades per student and evaluation instance
The system SHALL allow an authenticated teacher to register or update one grade for each student and evaluation instance in a course.

#### Scenario: Successful numeric grade registration
- **WHEN** an authenticated teacher submits a numeric grade greater than or equal to `0` for a student in a course evaluation instance
- **THEN** the system saves the grade and recalculates the student's averages for that course

#### Scenario: Reject negative grade
- **WHEN** an authenticated teacher submits a grade lower than `0`
- **THEN** the system MUST reject the grade and report that negative grades are not allowed

#### Scenario: Reject non-numeric grade
- **WHEN** an authenticated teacher submits a grade value containing invalid non-numeric content
- **THEN** the system MUST reject the grade and report the format error

### Requirement: Empty grades are represented as empty but calculated as zero
The system MUST preserve the distinction between an empty grade in the workflow and its mathematical treatment in averages.

#### Scenario: Empty grade affects averages as zero
- **WHEN** an authenticated teacher saves or leaves a grade value empty for a student evaluation instance
- **THEN** the system keeps that grade visually identifiable as empty and MUST calculate it as `0` in the student's averages

### Requirement: Course averages stay synchronized with grade and program changes
The system MUST keep the course detail averages synchronized whenever grades or valid program weights change.

#### Scenario: Averages recalculate after grade change
- **WHEN** a grade is created or updated for a student in a course
- **THEN** the system recalculates and exposes the student's general and weighted averages in the course detail view

#### Scenario: Averages recalculate after valid weight change
- **WHEN** the teacher saves a valid update to evaluation weights in a course program that already has grades recorded
- **THEN** the system recalculates the affected students' averages using the updated weights
