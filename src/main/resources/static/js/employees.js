// API endpoints
const API_BASE_URL = '';
const ENDPOINTS = {
    auth: `${API_BASE_URL}/auth`,
    employee: `${API_BASE_URL}/employee`,
    manager: `${API_BASE_URL}/manager`,
    admin: `${API_BASE_URL}/admin`
};

// DOM Elements
const employeesTableBody = document.getElementById('employeesTableBody');
const employeeForm = document.getElementById('employeeForm');
const employeeModal = document.getElementById('employeeModal');
const modalTitle = document.getElementById('modalTitle');
const searchInput = document.querySelector('.search-bar input');

// State
let currentEmployeeId = null;

// Fetch and display employees
async function fetchEmployees() {
    try {
        const response = await fetch(`${ENDPOINTS.admin}/viewallemployees`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        const employees = await response.json();
        displayEmployees(employees);
    } catch (error) {
        console.error('Error fetching employees:', error);
        showError('Failed to load employees');
    }
}

// Display employees in the table
function displayEmployees(employees) {
    employeesTableBody.innerHTML = employees.map(employee => `
        <tr>
            <td>${employee.id}</td>
            <td>${employee.name}</td>
            <td>${employee.department}</td>
            <td>${employee.designation}</td>
            <td>${employee.email}</td>
            <td>${employee.accountstatus}</td>
            <td class="action-buttons">
                <button class="action-btn edit" onclick="editEmployee(${employee.id})">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="action-btn delete" onclick="deleteEmployee(${employee.id})">
                    <i class="fas fa-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

// Open modal for adding new employee
function openAddEmployeeModal() {
    currentEmployeeId = null;
    modalTitle.textContent = 'Add Employee';
    employeeForm.reset();
    employeeModal.style.display = 'block';
}

// Open modal for editing employee
async function editEmployee(id) {
    try {
        const response = await fetch(`${ENDPOINTS.employee}/viewprofile?empid=${id}`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        const employee = await response.json();
        
        currentEmployeeId = id;
        modalTitle.textContent = 'Edit Employee';
        
        // Fill form with employee data
        document.getElementById('name').value = employee.name;
        document.getElementById('gender').value = employee.gender;
        document.getElementById('age').value = employee.age;
        document.getElementById('designation').value = employee.designation;
        document.getElementById('department').value = employee.department;
        document.getElementById('salary').value = employee.salary;
        document.getElementById('email').value = employee.email;
        document.getElementById('contact').value = employee.contact;
        
        employeeModal.style.display = 'block';
    } catch (error) {
        console.error('Error fetching employee details:', error);
        showError('Failed to load employee details');
    }
}

// Close modal
function closeModal() {
    employeeModal.style.display = 'none';
    employeeForm.reset();
    currentEmployeeId = null;
}

// Handle form submission
employeeForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const formData = {
        name: document.getElementById('name').value,
        gender: document.getElementById('gender').value,
        age: parseInt(document.getElementById('age').value),
        designation: document.getElementById('designation').value,
        department: document.getElementById('department').value,
        salary: parseFloat(document.getElementById('salary').value),
        email: document.getElementById('email').value,
        contact: document.getElementById('contact').value
    };
    
    try {
        if (currentEmployeeId) {
            // Update existing employee
            await fetch(`${ENDPOINTS.employee}/updateprofile`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
                body: JSON.stringify({...formData, id: currentEmployeeId})
            });
        } else {
            // Add new employee
            await fetch(`${ENDPOINTS.employee}/addemployee`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
                body: JSON.stringify(formData)
            });
        }
        
        closeModal();
        fetchEmployees();
        showSuccess(currentEmployeeId ? 'Employee updated successfully' : 'Employee added successfully');
    } catch (error) {
        console.error('Error saving employee:', error);
        showError('Failed to save employee');
    }
});

// Delete employee
async function deleteEmployee(id) {
    if (!confirm('Are you sure you want to delete this employee?')) return;
    
    try {
        await fetch(`${ENDPOINTS.admin}/deleteemployee?eid=${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        
        fetchEmployees();
        showSuccess('Employee deleted successfully');
    } catch (error) {
        console.error('Error deleting employee:', error);
        showError('Failed to delete employee');
    }
}

// Search functionality
searchInput.addEventListener('input', debounce(async (e) => {
    const searchTerm = e.target.value.trim();
    if (searchTerm.length < 2) {
        fetchEmployees();
        return;
    }
    
    try {
        const response = await fetch(`${ENDPOINTS.employee}/viewprofile?empid=${searchTerm}`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        const employee = await response.json();
        if (employee) {
            displayEmployees([employee]);
        }
    } catch (error) {
        console.error('Error searching employees:', error);
        showError('Failed to search employees');
    }
}, 300));

// Debounce function
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Show success message
function showSuccess(message) {
    // Implement your preferred notification system
    alert(message);
}

// Show error message
function showError(message) {
    // Implement your preferred notification system
    alert('Error: ' + message);
}

// Close modal when clicking outside
window.onclick = function(event) {
    if (event.target === employeeModal) {
        closeModal();
    }
};

// Initialize the page
document.addEventListener('DOMContentLoaded', fetchEmployees); 