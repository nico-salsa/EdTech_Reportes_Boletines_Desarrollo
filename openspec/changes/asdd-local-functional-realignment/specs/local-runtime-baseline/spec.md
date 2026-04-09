## ADDED Requirements

### Requirement: Backend runtime is reproducible in a local developer environment
The system SHALL provide a documented local backend runtime that compiles and starts using the repository configuration and local SQLite storage.

#### Scenario: Backend compiles from the repository
- **WHEN** a developer executes the documented backend validation command from a supported local environment
- **THEN** the backend build completes successfully without source-level compilation errors

#### Scenario: Backend starts with local persistence
- **WHEN** a developer follows the documented local backend startup steps
- **THEN** the API starts against the configured local SQLite database path without requiring external infrastructure

### Requirement: Frontend runtime is reproducible in a local developer environment
The system SHALL provide a documented local frontend runtime that builds and starts against the backend API contract.

#### Scenario: Frontend builds from the repository
- **WHEN** a developer executes the documented frontend build command from a supported local environment
- **THEN** the frontend build completes successfully using the repository dependencies and environment defaults

#### Scenario: Frontend can target the documented local API
- **WHEN** a developer starts the frontend using the documented local setup
- **THEN** the application resolves the documented API base URL and can interact with the local backend without additional undocumented configuration

### Requirement: Local setup instructions reflect the real repository structure
The system MUST document the exact prerequisites, commands, paths, and generated artifacts required to run the MVP locally.

#### Scenario: Local execution guide matches the repository
- **WHEN** a maintainer reviews the local execution documentation against the repository structure and runtime commands
- **THEN** the guide lists the real versions, folders, commands, ports, database path, and optional environment variables used by the project
