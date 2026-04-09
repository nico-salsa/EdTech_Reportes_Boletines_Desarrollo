## ADDED Requirements

### Requirement: Teacher can export an individual student report in the supported formats
The system SHALL allow an authenticated teacher to generate an individual student report for a course in `PDF`, `HTML`, and `JSON`.

#### Scenario: Successful report export
- **WHEN** an authenticated teacher selects a student report export and chooses one of the supported formats `PDF`, `HTML`, or `JSON`
- **THEN** the system generates and returns the report in the selected format with the student's course grades and averages

#### Scenario: Unsupported format is rejected
- **WHEN** an authenticated teacher requests an individual report in a format outside the supported set `PDF`, `HTML`, or `JSON`
- **THEN** the system MUST reject the request and report that the format is unsupported

### Requirement: Exported report contains the academic data required by the MVP
The system MUST include the minimum academic content needed for a simple but correct individual report.

#### Scenario: Report contains student, course, program, and averages
- **WHEN** the system generates an individual student report
- **THEN** the report MUST contain the teacher's course context, the student's identification, the evaluation instances, the recorded grades, and the student's final averages for that course

### Requirement: Export warns before generating reports with incomplete grading data
The system MUST warn the teacher before generating a report when the selected student has at least one empty grade.

#### Scenario: Warning shown for incomplete report data
- **WHEN** an authenticated teacher requests a student report for a course where the student has one or more empty grades
- **THEN** the system MUST display a warning before export and only proceed after the teacher confirms the generation
