## ADDED Requirements

### Requirement: Teacher can define an evaluation program for a course
The system SHALL allow an authenticated teacher to create the set of evaluation instances for a course using named activities with weighted percentages.

#### Scenario: Successful program definition
- **WHEN** an authenticated teacher submits one or more evaluation instances where every name is non-empty, unique within the course, every weight is greater than `0`, and the total sum is exactly `100%`
- **THEN** the system saves the course program and exposes it in the course detail view

#### Scenario: Reject program whose total weight is not exactly 100 percent
- **WHEN** an authenticated teacher submits evaluation instances whose weights do not sum exactly to `100%`
- **THEN** the system MUST reject the program and report that the total weight is invalid

#### Scenario: Reject program with invalid instance definitions
- **WHEN** an authenticated teacher submits a program containing an empty name, a duplicated name, or a weight less than or equal to `0`
- **THEN** the system MUST reject the program and report the invalid instance definitions

### Requirement: Teacher can update evaluation instances in an existing program
The system SHALL allow an authenticated teacher to update the name or weight of an existing evaluation instance while preserving the course program validity rules.

#### Scenario: Successful instance update
- **WHEN** an authenticated teacher edits an instance and the resulting program still has non-empty unique names, weights greater than `0`, and a total sum of exactly `100%`
- **THEN** the system saves the updated program and reflects the updated instance definitions in the course detail view

#### Scenario: Reject invalid update to instance definitions
- **WHEN** an authenticated teacher edits an instance and the resulting program contains duplicated names, empty names, weights less than or equal to `0`, or a total sum different from `100%`
- **THEN** the system MUST reject the update and preserve the last valid program definition

### Requirement: Teacher can remove evaluation instances from a program
The system SHALL allow an authenticated teacher to remove an evaluation instance from a course program as long as the remaining program is resubmitted as a valid `100%` program.

#### Scenario: Successful instance removal with valid remaining weights
- **WHEN** an authenticated teacher removes an evaluation instance and updates the remaining weights so the total remains exactly `100%`
- **THEN** the system saves the new program and removes the deleted instance from the course detail view

#### Scenario: Reject instance removal that leaves an invalid total
- **WHEN** an authenticated teacher removes an evaluation instance and the resulting program total is not exactly `100%`
- **THEN** the system MUST reject the change and preserve the last valid program definition
