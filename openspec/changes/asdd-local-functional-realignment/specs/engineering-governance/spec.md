## ADDED Requirements

### Requirement: Repository documentation reflects the current state of the system
The repository SHALL publish engineering documents that describe the current architecture, active workflow, local execution model, and supported MVP scope without referencing obsolete implementation states as if they were current.

#### Scenario: Core technical documents are current
- **WHEN** a maintainer reviews the root README and project documentation set
- **THEN** the documents identify the actual active OpenSpec change flow, current repository structure, supported MVP stories, and the real local execution model

### Requirement: Repository maintains an explicit AI-native workflow record
The repository SHALL document how AI-assisted work is governed, which artifacts are authoritative, and how specification, implementation, and validation are expected to interact.

#### Scenario: AI workflow is documented
- **WHEN** a contributor reviews the engineering workflow documentation
- **THEN** the repository provides a dedicated artifact that explains the active AI workflow, human review responsibility, and the order in which specs, design, tasks, code, and validation evidence are produced

### Requirement: Repository maintains traceability and retrospective evidence for each significant change
The repository SHALL preserve explicit links between scope, specs, implementation, validation evidence, and observed deviations.

#### Scenario: Traceability artifacts exist for the change
- **WHEN** a maintainer reviews the project documentation for a completed or in-progress change
- **THEN** the repository contains a traceability artifact and a reality check artifact that record what was planned, what was implemented, what diverged, and what debt remains
