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

### Requirement: Teacher can replace the evaluation program with a validated full submission
The system SHALL allow an authenticated teacher to save the full course program in one request, updating existing activities, creating new ones, and removing omitted ones when the submitted program is still valid.

#### Scenario: Successful full program replacement
- **WHEN** an authenticated teacher saves a revised activity list where names remain non-empty and unique, every weight is greater than `0`, and the submitted total is exactly `100%`
- **THEN** the system persists the revised program and reflects the updated activities in the course detail view

#### Scenario: Omitted activity is removed during full program replacement
- **WHEN** an authenticated teacher saves a valid revised activity list that no longer includes one of the previously stored activities
- **THEN** the system removes that activity from the course program and deletes the grades associated with that removed activity

#### Scenario: Reject invalid full program replacement
- **WHEN** an authenticated teacher saves a revised activity list containing duplicated names, empty names, weights less than or equal to `0`, or a total sum different from `100%`
- **THEN** the system MUST reject the update and preserve the last valid program definition

### Requirement: Backend supports direct removal of an individual evaluation instance
The system SHALL allow an authenticated client to remove a single stored evaluation activity through the dedicated deletion endpoint as long as at least one activity remains in the course.

#### Scenario: Successful direct activity deletion
- **WHEN** an authenticated client deletes one stored evaluation activity from a course that still has at least one additional activity
- **THEN** the system removes that activity, deletes its recorded grades, reorders the remaining activity positions, and returns the updated course detail

#### Scenario: Reject deletion of the last remaining activity
- **WHEN** an authenticated client tries to delete the only remaining evaluation activity of a course
- **THEN** the system MUST reject the request and preserve the existing activity

#### Scenario: Direct deletion can leave an interim incomplete total
- **WHEN** an authenticated client uses the direct deletion endpoint and the remaining stored weights no longer sum to exactly `100%`
- **THEN** the system still returns the updated course state and the course workflow can show the program as incomplete until a valid full replacement is saved
