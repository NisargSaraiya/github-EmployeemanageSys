# Employee Management System

## Overview

The Employee Management System is a web-based platform to manage employees, managers, leave applications, and duties. It features secure authentication, role-based access, and a modern, responsive UI for admins, managers, and employees.

---

## Features

- Secure login (JWT authentication)
- Role-based admin, manager, and employee panels
- Employee and manager management
- Leave application workflow
- Duties tracking
- Account settings

---

## Screenshots

> _Place your screenshots in `docs/images/` and use the filenames below._

### Login Page
![Login Page](docs/images/login.png)

### Admin Dashboard
![Admin Dashboard](docs/images/admin_dashboard.png)

### Employee Management
![Employee Management](docs/images/employee_management.png)

### Manager Management
![Manager Management](docs/images/manager_management.png)

### Settings Page
![Settings Page](docs/images/settings.png)

---

## Setup & Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/EmployeeManagementSystem.git
   cd EmployeeManagementSystem
   ```

2. **Configure the database:**
   - Edit `src/main/resources/application.properties` with your DB settings.

3. **Build the project:**
   - Maven:
     ```bash
     mvn clean install
     ```
   - Gradle:
     ```bash
     gradle build
     ```

4. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```
   or
   ```bash
   gradle bootRun
   ```

5. **Access the application:**
   - Open your browser and go to `http://localhost:2027/`

---

## Feature-Based Code Snippets

### 1. User Authentication (JWT Login)

**Backend:**

```java
// JWTUtilizer.java
public String generateToken(UserDetails userDetails) { /* ... */ }
public Boolean validateToken(String token, UserDetails userDetails) { /* ... */ }
```

**Frontend:**

```html
<!-- login.html -->
<form id="loginForm">
    <input type="text" name="username" placeholder="Username" required />
    <input type="password" name="password" placeholder="Password" required />
    <button type="submit">Login</button>
</form>
<script>
document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    const formData = new FormData(this);
    const response = await fetch('/api/auth/login', {
        method: 'POST',
        body: JSON.stringify({
            username: formData.get('username'),
            password: formData.get('password')
        }),
        headers: { 'Content-Type': 'application/json' }
    });
    const data = await response.json();
    if (data.token) {
        localStorage.setItem('jwt', data.token);
        // Redirect or update UI
    }
});
</script>
```

---

### 2. Role-Based Access

**Backend:**

```java
// ManagerService.java
@PreAuthorize("hasRole('MANAGER')")
public List<Employee> getTeamMembers(Long managerId) { /* ... */ }
```

**Frontend:**

```javascript
// manager.js
const userRole = localStorage.getItem('role');
if (userRole === 'ADMIN') {
    document.getElementById('adminPanel').style.display = 'block';
} else {
    document.getElementById('adminPanel').style.display = 'none';
}
```

---

### 3. Employee CRUD Operations

**Backend:**

```java
// EmployeeService.java
public Employee saveEmployee(Employee employee) {
    return employeeRepository.save(employee);
}
```

**Frontend:**

```javascript
// manager.js
fetch('/api/employees', {
    headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwt') }
})
.then(response => response.json())
.then(data => {
    const table = document.getElementById('employeeTable');
    data.forEach(emp => {
        const row = document.createElement('tr');
        row.innerHTML = `<td>${emp.name}</td><td>${emp.role}</td>`;
        table.appendChild(row);
    });
});
```

```html
<!-- employees.html -->
<table id="employeeTable">
    <tr>
        <th>Name</th>
        <th>Role</th>
    </tr>
    <!-- Rows will be populated by JS -->
</table>
```

---

### 4. Manager Dashboard

**Backend:**

```java
// ManagerService.java
public List<Employee> getTeamMembers(Long managerId) { /* ... */ }
```

**Frontend:**

```javascript
// manager.js
function loadTeam() {
    fetch('/api/manager/team', {
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwt') }
    })
    .then(res => res.json())
    .then(team => {
        // Render team members in dashboard
    });
}
```

---

### 5. Admin Controls

**Backend:**

```java
// AdminService.java
public void assignRoleToUser(Long userId, String role) { /* ... */ }
```

**Frontend:**

```html
<select id="userRoleSelect">
    <option value="EMPLOYEE">Employee</option>
    <option value="MANAGER">Manager</option>
    <option value="ADMIN">Admin</option>
</select>
<button onclick="assignRole()">Assign Role</button>
<script>
function assignRole() {
    const userId = ...; // get from UI
    const role = document.getElementById('userRoleSelect').value;
    fetch(`/api/admin/assign-role`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('jwt')
        },
        body: JSON.stringify({ userId, role })
    });
}
</script>
```

---

### 6. Settings Page

```html
<!-- settings section -->
<form id="settingsForm">
    <input type="text" name="username" />
    <input type="email" name="email" />
    <input type="password" name="currentPassword" />
    <input type="password" name="newPassword" />
    <button type="submit">Update Settings</button>
</form>
```

---

## Contribution Guidelines

1. Fork the repository
2. Create a new branch (`git checkout -b feature/your-feature`)
3. Commit your changes
4. Push to the branch (`git push origin feature/your-feature`)
5. Create a pull request

---

## License

_Add your license here (e.g., MIT, Apache 2.0, etc.)_

---

## Contact

For questions or support, contact [your-email@example.com].

---

**How to add your images:**  
1. Save your screenshots as `login.png`, `admin_dashboard.png`, `employee_management.png`, `manager_management.png`, `settings.png`.
2. Place them in a folder like `docs/images/` in your project.
3. The markdown above will render them in your documentation.

If you want the file as plain text, just copy this content and save it as `README.md`!  
Let me know if you need any more customization or want to add more features/screenshots.
