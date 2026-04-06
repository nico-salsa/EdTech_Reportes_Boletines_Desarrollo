## ADDED Requirements

### Requirement: Teacher can register with local credentials
The system SHALL allow a teacher to create a local account using a unique `username` and a `password`.

#### Scenario: Successful registration
- **WHEN** a teacher submits a non-empty unique `username` and a non-empty `password`
- **THEN** the system creates the account and stores the credentials for future authentication

#### Scenario: Registration rejected for incomplete input
- **WHEN** a teacher submits the registration form with an empty `username` or empty `password`
- **THEN** the system MUST reject the registration and report the missing required field

#### Scenario: Registration rejected for duplicate username
- **WHEN** a teacher submits a `username` that already exists in the local credential store
- **THEN** the system MUST reject the registration and report that the `username` is already in use

### Requirement: Teacher can start a local authenticated session
The system SHALL authenticate a teacher with local credentials and grant access only after successful login.

#### Scenario: Successful login
- **WHEN** a teacher submits a registered `username` with its matching `password`
- **THEN** the system creates a local authenticated session and enables access to protected application routes

#### Scenario: Login rejected for invalid credentials
- **WHEN** a teacher submits a `username` or `password` that does not match a registered account
- **THEN** the system MUST deny access and show a generic invalid credentials response

#### Scenario: Login rejected for incomplete input
- **WHEN** a teacher submits the login form with an empty `username` or empty `password`
- **THEN** the system MUST reject the request and report the missing required field

### Requirement: Protected views require an authenticated session
The system MUST restrict teacher-only views to authenticated sessions.

#### Scenario: Protected route access without session
- **WHEN** a user tries to open a protected teacher view without a valid authenticated session
- **THEN** the system MUST deny access and redirect the user to the login flow
