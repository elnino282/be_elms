# Employee Leave Management System (ELMS) - Backend API

A Spring Boot REST API for managing employee leave requests with two-role authorization (Employee and Admin).

## 🚀 Technology Stack

- **Java 17**
- **Spring Boot 3.5.7**
- **Spring Data JPA**
- **PostgreSQL**
- **Flyway** (Database migration)
- **Lombok** (Code simplification)

## 📋 Features

### Employee Role
- View own leave balance
- Create leave requests
- View own leave requests

### Admin Role
- All Employee permissions
- View all leave requests
- Approve/Reject leave requests
- View all employees' leave balances

## 🗄️ Database Setup

1. Create PostgreSQL database:
```sql
CREATE DATABASE ELMS;
```

2. Update `application.properties` if needed:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ELMS
spring.datasource.username=postgres
spring.datasource.password=your_password
```

3. Flyway will automatically create tables and seed data on startup.

## 🔐 Default Login Accounts

- **Admin**: username=`admin`, password=`admin`
- **Employee**: username=`employee`, password=`employee`

## 🛠️ Running the Application

```bash
# Using Maven wrapper
./mvnw spring-boot:run

# Or on Windows
mvnw.cmd spring-boot:run
```

The API will be available at: `http://localhost:8080`

## 📡 API Endpoints

### Authentication

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin"
}

Response:
{
  "success": true,
  "message": "Login successful",
  "data": {
    "id": 1,
    "username": "admin",
    "fullName": "System Administrator",
    "role": "ADMIN"
  }
}
```

### Leave Requests

#### Create Leave Request (Employee)
```http
POST /api/leave-requests?employeeId=2
Content-Type: application/json

{
  "startDate": "2025-12-20",
  "endDate": "2025-12-22",
  "reason": "Family vacation"
}
```

#### Get My Requests (Employee)
```http
GET /api/leave-requests/my-requests?employeeId=2
```

#### Get All Requests (Admin Only)
```http
GET /api/leave-requests
```

#### Approve Request (Admin Only)
```http
PUT /api/leave-requests/{id}/approve?adminId=1
```

#### Reject Request (Admin Only)
```http
PUT /api/leave-requests/{id}/reject?adminId=1
Content-Type: application/json

{
  "rejectionReason": "Insufficient staffing during that period"
}
```

### Leave Balances

#### Get My Balance (Employee)
```http
GET /api/leave-balances/my-balance?employeeId=2
# Optional: specify year
GET /api/leave-balances/my-balance?employeeId=2&year=2025
```

#### Get All Balances (Admin Only)
```http
GET /api/leave-balances
# Optional: specify year (defaults to current year)
GET /api/leave-balances?year=2025
```

## 🔒 Business Rules

### Creating Leave Requests
- End date must be >= start date
- Cannot overlap with existing approved leave
- Must have sufficient leave balance
- Automatically creates leave balance for the year if it doesn't exist

### Approving Requests
- Only ADMIN can approve
- Can only approve PENDING requests
- Updates leave balance (used days)
- Records approver and decision timestamp

### Rejecting Requests
- Only ADMIN can reject
- Can only reject PENDING requests
- Rejection reason is required
- Does NOT update leave balance

## 📊 Database Schema

### Employees
- Stores user credentials and profile
- Roles: EMPLOYEE, ADMIN

### Leave Balances
- Tracks yearly leave entitlement and usage
- Auto-calculated remaining days

### Leave Requests
- Stores leave applications
- Status: PENDING, APPROVED, REJECTED
- Auto-calculated total days and balance year

## 🧪 Testing the API

You can test the API using:
- **Postman** - Import the endpoints listed above
- **cURL** - Use command-line requests
- **Thunder Client** (VS Code extension)

Example cURL:
```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"employee","password":"employee"}'

# Create leave request
curl -X POST "http://localhost:8080/api/leave-requests?employeeId=2" \
  -H "Content-Type: application/json" \
  -d '{"startDate":"2025-12-20","endDate":"2025-12-22","reason":"Vacation"}'
```

## 📁 Project Structure

```
src/main/java/org/example/be_elms/
├── config/              # Configuration classes
├── controller/          # REST API controllers
├── dto/                 # Data Transfer Objects
├── exception/           # Custom exceptions and handlers
├── model/
│   ├── entity/         # JPA entities
│   └── enums/          # Enum types
├── repository/         # Spring Data repositories
├── service/            # Business logic layer
└── util/               # Utility classes

src/main/resources/
├── db/migration/       # Flyway migration scripts
└── application.properties
```

## 🔧 Configuration

Key application properties:
- `server.port=8080` - Server port
- `spring.jpa.hibernate.ddl-auto=none` - Let Flyway handle schema
- `spring.flyway.enabled=true` - Enable Flyway migrations
- `spring.jpa.show-sql=true` - Show SQL queries in logs

## ⚠️ Important Notes

1. **Password Storage**: Currently using plain text passwords for demo purposes. In production, use BCrypt hashing.
2. **Authentication**: Using simple request parameters for user identification. In production, implement proper JWT or session-based authentication.
3. **Authorization**: Role-based access control is implemented at the service layer. In production, use Spring Security annotations.

## 🐛 Troubleshooting

### Database Connection Issues
- Ensure PostgreSQL is running
- Verify database credentials in `application.properties`
- Check if ELMS database exists

### Flyway Migration Errors
- Clear the database and restart: `DROP DATABASE ELMS; CREATE DATABASE ELMS;`
- Check Flyway metadata table: `SELECT * FROM flyway_schema_history;`

### Linting Warnings
- Null-safety warnings are expected and won't affect functionality
- They can be resolved by adding null-safety annotations if needed

## 📝 License

This is a demo project for educational purposes.

