## ADDED Requirements

### Requirement: Critical local validations are executable before integration
The repository SHALL define and execute a minimum validation set covering active OpenSpec artifacts, backend build health, and frontend build health before a change is considered ready to merge.

#### Scenario: Local validation set is defined
- **WHEN** a contributor prepares a change for integration
- **THEN** the repository identifies the commands and expected pass criteria for spec validation, backend validation, and frontend validation

#### Scenario: Merge readiness depends on passing validations
- **WHEN** one or more required validations fail
- **THEN** the change MUST NOT be considered ready for merge until the failures are resolved or explicitly documented as accepted risk

### Requirement: Critical MVP flows have automated regression coverage
The repository SHALL provide automated checks for the most critical MVP behaviors covering runtime stability and business flows with the highest regression risk.

#### Scenario: Critical automated tests are present
- **WHEN** a maintainer reviews the automated validation assets
- **THEN** the repository contains executable tests or smoke validations for authentication, course management, grade handling, and report export or documents a temporary risk with a remediation task

### Requirement: Delivery artifacts include merge and NFR evidence
The repository MUST capture the operational evidence required to judge whether a change is ready to integrate under ASDD expectations.

#### Scenario: Change package includes delivery evidence
- **WHEN** a contributor marks a change as ready for review
- **THEN** the repository includes a merge checklist, current test evidence, and an NFR-oriented summary covering at least local portability, basic security posture, observability limitations, and maintainability risks
