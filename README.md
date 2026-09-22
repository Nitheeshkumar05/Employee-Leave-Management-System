# ABC Corp Employee Leave Management System

A realistic full-stack **Employee Leave Management System** designed around a common company workflow: employees submit leave requests, HR reviews them, and the final decision plus HR's short note is stored permanently for future records.

> Portfolio project: the application models a real company workflow, but it is intended for local development/demo use rather than claiming to be production-certified HR software.

## 1. Business Problem

In a small or medium company, employees may currently request leave through phone calls or chat messages. That makes it difficult to maintain a reliable record of:

- Who requested leave
- Which dates were requested
- Why the employee requested leave
- Whether HR approved or rejected it
- What HR communicated back to the employee
- When the request was reviewed

This project replaces that manual process with a centralized web application.

## 2. Core Workflow

```text
Employee registers
       |
       v
Employee logs in
       |
       v
JWT authentication
       |
       v
Employee submits leave request
       |
       v
Status = PENDING
       |
       v
HR dashboard receives the request
       |
       +-----------------------+
       |                       |
       v                       v
    APPROVE                 REJECT
       |                       |
       +-----------+-----------+
                   |
                   v
          HR writes short note
                   |
                   v
       Decision + note saved in MySQL
                   |
                   v
          Employee sees result
```

### Example HR notification

> **Kamalesh has requested 3 days of CASUAL leave from 25 Sep 2026 to 27 Sep 2026.**

HR can approve it with:

> Leave approved. Enjoy your time off!

Or reject it with:

> Sorry Kamalesh, we cannot approve this request because your leave balance has already been used this month.

The employee sees the status and the exact HR note on the leave history page.

## 3. Roles and Permissions

### EMPLOYEE

- Register an employee account
- Login using email and password
- View personal dashboard
- Apply for leave
- View own leave requests
- View `PENDING`, `APPROVED`, and `REJECTED` status
- Read HR's decision note
- Cancel a request while it is still pending

### HR

- Login using HR credentials
- View organization dashboard
- View all employees
- Create, update and remove employee records
- Manage departments
- View all leave requests
- Filter/search leave requests
- Approve pending requests
- Reject pending requests
- Add a short HR note during approval/rejection
- Store reviewer and review timestamp

Role selection is **not exposed on public registration**. New registrations are always `EMPLOYEE`. HR access is controlled by the backend.

## 4. Technology Stack

### Backend

| Technology | Version / Choice | Purpose |
|---|---|---|
| Java | 21 | Backend language |
| Spring Boot | 3.5.4 | Application framework |
| Spring Web | 3.5.4 | REST APIs |
| Spring Data JPA | 3.5.4 | Database access |
| Hibernate | Managed by Spring Boot | ORM |
| Spring Security | 6.x via Spring Boot | Authentication and authorization |
| JWT | JJWT 0.12.6 | Stateless authentication |
| MySQL | 8.x | Relational database |
| Maven | 3.9+ recommended | Build and dependency management |
| Lombok | Spring-managed | Boilerplate reduction |

### Frontend

| Technology | Version / Choice | Purpose |
|---|---|---|
| React | 19.1.1 | UI |
| Vite | 7.1.2 | Frontend build tool |
| React Router | 7.8.2 | Client-side routing |
| Axios | 1.11.0 | REST API communication |
| Lucide React | 0.468.0 | UI icons |
| HTML5 | - | Semantic structure |
| CSS3 | - | Responsive modern UI |

## 5. Project Architecture

```text
employee-leave-management/
│
├── Backend/
│   └── employee-leave-management/
│       ├── pom.xml
│       └── src/main/java/com/example/employeemanagement/
│           ├── config/
│           ├── controller/
│           ├── dto/
│           ├── exception/
│           ├── model/
│           ├── repository/
│           ├── security/
│           └── service/
│
├── Frontend/
│   ├── package.json
│   ├── index.html
│   └── src/
│       ├── components/
│       ├── context/
│       ├── pages/
│       ├── services/
│       ├── App.jsx
│       ├── main.jsx
│       └── styles.css
│
├── database.sql
├── requirements.txt
└── README.md
```

### Backend request flow

```text
React UI
   |
   | Axios + Bearer JWT
   v
Spring Security / JWT Filter
   |
   v
REST Controller
   |
   v
Service Layer
   |
   v
Repository Layer
   |
   v
JPA / Hibernate
   |
   v
MySQL
```

The project keeps the responsibilities separate:

- **Controller**: receives HTTP requests and returns responses.
- **DTO**: controls the request/response data exchanged through the API.
- **Service**: contains business rules and workflow logic.
- **Repository**: communicates with MySQL through JPA.
- **Entity/Model**: represents persistent database data.
- **Security**: validates JWTs and applies role-based authorization.

## 6. Database Design

### employees

```text
employees
---------
id PK
name
email UNIQUE
phone
password
role
 department_id FK -> departments.id
```

### departments

```text
departments
-----------
id PK
name UNIQUE
```

### leave_requests

```text
leave_requests
--------------
id PK
employee_id FK -> employees.id
leave_type
start_date
end_date
reason
status
hr_note
reviewed_by FK -> employees.id
requested_at
reviewed_at
```

### Relationships

```text
Department 1 -------- * Employee

Employee 1 ---------- * LeaveRequest

Employee(HR) 1 ------ * LeaveRequest.reviewed_by
```

This allows an employee to submit many leave requests and allows an HR employee to be recorded as the person who reviewed a request.

## 7. Leave State Machine

```text
                +-----------+
                |  PENDING  |
                +-----------+
                  /       \
                 /         \
                v           v
        +-----------+   +-----------+
        | APPROVED  |   | REJECTED  |
        +-----------+   +-----------+
```

Only `PENDING` requests can be reviewed.

Only `PENDING` requests can be cancelled by the employee.

Once HR approves or rejects a request, the system stores:

- Final status
- HR note
- HR reviewer
- Review timestamp

## 8. JWT Security Flow

```text
1. Employee enters email + password
             |
             v
2. POST /api/auth/login
             |
             v
3. AuthenticationManager
             |
             v
4. UserDetailsService loads employee from MySQL
             |
             v
5. BCrypt verifies password
             |
             v
6. JwtService creates JWT
             |
             v
7. React stores JWT in localStorage
             |
             v
8. Axios adds:
   Authorization: Bearer <JWT>
             |
             v
9. JwtAuthenticationFilter validates token
             |
             v
10. Spring Security checks EMPLOYEE / HR role
             |
             v
11. Controller is allowed or rejected
```

### Why JWT?

The backend is stateless: the server does not keep an HTTP login session for the React client. Each protected API request carries the JWT, and Spring Security validates it before the request reaches the protected controller.

### Important security note

This portfolio version stores the JWT in browser `localStorage` because it keeps the React + Spring Boot authentication flow easy to demonstrate. For a production deployment, an architecture using secure, `HttpOnly`, `SameSite` cookies and an appropriate CSRF strategy should be evaluated.

Passwords are never stored as plain text; they are encoded with BCrypt.

## 9. REST API Design

### Authentication

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register employee |
| POST | `/api/auth/login` | Public | Authenticate and return JWT |

### Employees

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| GET | `/api/employees` | HR | List/search employees |
| GET | `/api/employees/me` | Authenticated | Current employee profile |
| POST | `/api/employees` | HR | Create employee |
| PUT | `/api/employees/{id}` | HR | Update employee |
| DELETE | `/api/employees/{id}` | HR | Remove employee |

### Departments

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| GET | `/api/departments` | Authenticated | List departments |
| POST | `/api/departments` | HR | Create department |
| PUT | `/api/departments/{id}` | HR | Update department |
| DELETE | `/api/departments/{id}` | HR | Delete department |

### Leave requests

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| GET | `/api/leaves` | HR | View all requests |
| GET | `/api/leaves/pending` | HR | View pending requests |
| GET | `/api/leaves/my` | Employee | View own requests |
| POST | `/api/leaves` | Employee | Submit leave request |
| PUT | `/api/leaves/{id}/approve` | HR | Approve + save HR note |
| PUT | `/api/leaves/{id}/reject` | HR | Reject + save HR note |
| DELETE | `/api/leaves/{id}` | Employee | Cancel pending request |

## 10. Example Leave API Request

### Employee submits leave

```http
POST /api/leaves
Authorization: Bearer <JWT>
Content-Type: application/json
```

```json
{
  "leaveType": "CASUAL",
  "startDate": "2026-09-25",
  "endDate": "2026-09-27",
  "reason": "Personal work"
}
```

### HR approves

```http
PUT /api/leaves/15/approve
Authorization: Bearer <HR_JWT>
Content-Type: application/json
```

```json
{
  "note": "Leave approved. Enjoy your time off!"
}
```

### HR rejects

```http
PUT /api/leaves/15/reject
Authorization: Bearer <HR_JWT>
Content-Type: application/json
```

```json
{
  "note": "Sorry Kamalesh, we cannot approve this request because your leave balance has already been used this month."
}
```

## 11. Frontend Pages

### Public

- Login
- Employee Registration

### Employee

- My Dashboard
- My Leave Requests
- Request Leave

### HR

- HR Dashboard
- Leave Requests / Review Queue
- Employees
- Departments

The UI is intentionally designed as a clean internal company portal rather than a generic student CRUD screen. It includes:

- Responsive sidebar navigation
- Role-specific navigation
- KPI cards
- Search and filters
- Leave request cards
- Approval/rejection modal
- HR notes
- Employee directory
- Department management
- Empty states
- Error states
- Responsive mobile layout

## 12. Default HR Account

The application creates one HR account automatically on startup if it does not already exist:

```text
Email:    hr@abccorp.com
Password: hr123456
Role:     HR
```

Change demo credentials and the JWT secret before any real deployment.

## 13. Local Setup

### Prerequisites

- Java 21
- Maven 3.9+
- MySQL 8+
- Node.js 20+ recommended
- npm
- Git
- Postman optional

### Step 1: Create database

Run:

```sql
CREATE DATABASE employee_leave_db;
```

The application uses Hibernate `ddl-auto=update` to create/update the required tables.

### Step 2: Configure MySQL

Open:

```text
Backend/employee-leave-management/src/main/resources/application.properties
```

Set:

```properties
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

For a real deployment, move secrets to environment variables instead of committing them.

### Step 3: Start backend

```bash
cd Backend/employee-leave-management
mvn spring-boot:run
```

Backend:

```text
http://localhost:8080
```

### Step 4: Start frontend

```bash
cd Frontend
npm install
npm run dev
```

Frontend:

```text
http://localhost:5173
```

## 14. Interview Explanation

### One-minute project explanation

> "I developed an Employee Leave Management System for a company-style leave workflow. Employees can register, log in securely and submit leave requests with the leave type, dates and reason. The request is stored as PENDING in MySQL. HR has a separate role-based dashboard where they can view all requests and approve or reject them with a short note. The decision, HR note, reviewer and review timestamp are stored in the database, so employees can see the complete history from their dashboard. The frontend is built with React.js, while Spring Boot provides REST APIs, Spring Data JPA handles persistence, MySQL stores the data, and Spring Security with JWT provides authentication and role-based authorization."

### If asked: "What happens after an employee applies?"

> "The React frontend sends a POST request to the Spring Boot leave controller. The JWT identifies the logged-in employee. The service validates the dates, creates a LeaveRequest with PENDING status, and stores it through the JPA repository in MySQL. HR can then see the request in the HR dashboard."

### If asked: "How does HR approval work?"

> "The HR dashboard sends an approval or rejection request with an optional note. Spring Security first verifies that the JWT belongs to an HR user. The service checks that the request is still PENDING, changes the status, stores the HR note, reviewer and review timestamp, and saves the updated record in MySQL."

### If asked: "Why did you use DTOs?"

> "DTOs separate API request and response data from the database entities. They prevent me from exposing internal entity fields such as the password and let me control exactly what data the API accepts or returns."

### If asked: "Why did you use a service layer?"

> "The controller should mainly handle HTTP requests. Business rules such as validating leave dates, checking that only pending requests can be reviewed, and saving the HR decision belong in the service layer."

## 15. Important Business Rules

- Public registration always creates an `EMPLOYEE`.
- HR role is controlled by the backend.
- Email addresses are unique.
- Passwords are BCrypt encoded.
- Leave end date cannot be before start date.
- Leave cannot start in the past.
- New leave requests start as `PENDING`.
- Only HR can approve or reject requests.
- Only pending requests can be reviewed.
- Employees can cancel only their own pending requests.
- Approved/rejected requests remain stored as historical records.
- HR decisions include an optional note, reviewer and timestamp.

## 16. Future Enhancements

If this were expanded beyond the portfolio version, useful next features would include:

- Leave balance by leave type
- Automatic leave-balance calculation
- Email notifications
- Holiday calendar
- Manager + HR multi-level approval
- Audit log for every HR action
- Refresh tokens and token rotation
- HttpOnly cookie authentication
- Pagination for large employee/leave tables
- Automated tests and CI/CD
- Production environment variables and secret management

## 17. Project Status

The project is structured as a clean portfolio implementation with:

- React frontend
- Spring Boot REST backend
- MySQL persistence
- JWT authentication
- BCrypt password hashing
- Role-based authorization
- DTOs
- Service/repository architecture
- Leave approval workflow
- Persistent HR notes and review history
- Responsive company-style UI
