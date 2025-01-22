  # Support Team Management System

A backend system to assist the support team in managing their tasks and operations efficiently. The system includes role-based access control with two roles: **Manager** and **Support Member**.

---

## Features
- **Role-based access**:
  - **Manager**:
    - Assign project to support members.
    - Monitor task status.
    - - Add/remove support members to/from projects.
  - **Support Member**:
    - View assigned project.
- **Email Notifications**:
  - Send a confirmation email when a user registers.
  - Send email to all managers to activate support member account.
  - Send emails when a manager assigns or removes a support member from a project.
  - Alert emails sent to the manager before project expiration to ensure timely actions.
- **Project Management**:
  - Create, read, update, and delete tasks (CRUD operations).
  - Assign tasks to support members.
- **Scheduled Jobs**:
  - Automatic email reminders sent to managers before the expiration of projects.
- **Secure Login and Authentication**:
  - JWT-based authentication for secure login.

---

## Tech Stack
### Backend
- **Framework**: Spring Boot
- **Database**: MySQL
- **Authentication**: JWT-based authentication

---

## Installation and Setup

### Prerequisites
- Java 11 or higher
- MySQL

### Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/<your-username>/support-team-management.git
2. Navigate to the project folder:
   ```bash
   cd support-team-management

3. Configure the application.properties file with your MySQL database credentials.
4. Build and run the application:
   ```bash
   mvn spring-boot:run
   
### Usage
1. Access the backend APIs via tools like Postman or Swagger UI.
2. Log in using the following example credentials:
  - Manager: manager@example.com / password
  - Support Member: member@example.com / password

### Contact
  - Author: Omar Mohamed
  - LinkedIn: www.linkedin.com/in/omar-abo-elssad
  - Email: mo.omar753@gmail.com


