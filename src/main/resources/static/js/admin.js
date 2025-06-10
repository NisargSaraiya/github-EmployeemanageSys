// API Endpoints
const API_BASE_URL = 'http://localhost:2027';
const ENDPOINTS = {
    auth: '/auth',
    admin: '/admin',
    employee: '/employee',
    manager: '/manager'
};

// DOM Elements
const navItems = document.querySelectorAll('.nav-item');
const contentSections = document.querySelectorAll('.content');
const logoutBtn = document.getElementById('logoutBtn');
const adminName = document.getElementById('adminName');

// State
let currentEmployeeId = null;
let currentManagerId = null;

// Initialize dashboard
document.addEventListener('DOMContentLoaded', function() {
    // Check authentication first
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    // Set admin name
    const userData = JSON.parse(localStorage.getItem('userData'));
    if (userData) {
        adminName.textContent = userData.name;
    }

    // Navigation
    document.querySelectorAll('.nav-item').forEach(item => {
        item.addEventListener('click', function() {
            const page = this.dataset.page;
            showPage(page);
        });
    });

    // Add Employee Button
    const addEmployeeBtn = document.getElementById('addEmployeeBtn');
    if (addEmployeeBtn) {
        addEmployeeBtn.addEventListener('click', () => {
            openModal('employeeModal');
        });
    }

    // Add Manager Button
    const addManagerBtn = document.getElementById('addManagerBtn');
    if (addManagerBtn) {
        addManagerBtn.addEventListener('click', () => {
            openModal('managerModal');
        });
    }

    // Assign Duty Button
    const assignDutyBtn = document.getElementById('assignDutyBtn');
    if (assignDutyBtn) {
        assignDutyBtn.addEventListener('click', () => {
            openModal('dutyModal');
        });
    }

    // Logout Button
    if (logoutBtn) {
        logoutBtn.addEventListener('click', handleLogout);
    }

    // Initialize dashboard data
    initializeDashboard();
});

// Navigation handling
navItems.forEach(item => {
    item.addEventListener('click', (e) => {
        e.preventDefault();
        const targetPage = item.dataset.page;
        
        // Update active state
        navItems.forEach(nav => nav.classList.remove('active'));
        item.classList.add('active');
        
        // Show target content
        contentSections.forEach(section => {
            section.classList.add('hidden');
            if (section.id === targetPage) {
                section.classList.remove('hidden');
                // Load page-specific data
                switch(targetPage) {
                    case 'dashboard':
                        loadDashboardData();
                        break;
                    case 'employees':
                        loadEmployees();
                        break;
                    case 'managers':
                        loadManagers();
                        break;
                    case 'leaves':
                        loadLeaveApplications();
                        break;
                    case 'duties':
                        loadDuties();
                        break;
                }
            }
        });
    });
});

// Event Listeners
function setupEventListeners() {
    // Logout
    logoutBtn.addEventListener('click', () => {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        localStorage.removeItem('userData');
        window.location.href = 'login.html';
    });

    // Add Employee
    document.getElementById('addEmployeeBtn').addEventListener('click', () => {
        openModal('employeeModal');
    });

    // Add Manager
    document.getElementById('addManagerBtn').addEventListener('click', () => {
        openModal('managerModal');
    });

    // Assign Duty
    document.getElementById('assignDutyBtn').addEventListener('click', () => {
        openModal('dutyModal');
    });

    // Form Submissions
    document.getElementById('employeeForm').addEventListener('submit', handleEmployeeSubmit);
    document.getElementById('managerForm').addEventListener('submit', handleManagerSubmit);
    document.getElementById('dutyForm').addEventListener('submit', handleDutySubmit);
    document.getElementById('accountSettingsForm').addEventListener('submit', handleSettingsSubmit);
}

// Dashboard Functions
async function loadDashboardData() {
    try {
        // Load employee count
        const employeeResponse = await fetch(`${API_BASE_URL}${ENDPOINTS.admin}/employeecount`, {
            headers: getAuthHeaders()
        });
        if (employeeResponse.ok) {
            const employeeCount = await employeeResponse.json();
            document.getElementById('totalEmployees').textContent = employeeCount;
        }

        // Load manager count
        const managerResponse = await fetch(`${API_BASE_URL}${ENDPOINTS.admin}/managercount`, {
            headers: getAuthHeaders()
        });
        if (managerResponse.ok) {
            const managerCount = await managerResponse.json();
            document.getElementById('totalManagers').textContent = managerCount;
        }

        // Load pending leaves count
        const leavesResponse = await fetch(`${API_BASE_URL}${ENDPOINTS.admin}/viewallleaveApplications`, {
            headers: getAuthHeaders()
        });
        if (leavesResponse.ok) {
            const leaves = await leavesResponse.json();
            const pendingLeaves = leaves.filter(leave => leave.status === 'PENDING').length;
            document.getElementById('pendingLeaves').textContent = pendingLeaves;
        }

        // Load active duties count
        const dutiesResponse = await fetch(`${API_BASE_URL}${ENDPOINTS.admin}/viewassigneddutiesbyadmin`, {
            headers: getAuthHeaders()
        });
        if (dutiesResponse.ok) {
            const duties = await dutiesResponse.json();
            document.getElementById('activeDuties').textContent = duties.length;
        }

        // Load recent activities
        loadRecentActivities();
    } catch (error) {
        // Silently handle errors without showing popups
        console.error('Error loading dashboard data:', error);
    }
}

async function loadRecentActivities() {
    try {
        const activities = [];
        
        // Get recent leaves
        const leavesResponse = await fetch(`${API_BASE_URL}${ENDPOINTS.admin}/viewallleaveApplications`, {
            headers: getAuthHeaders()
        });
        if (leavesResponse.ok) {
            const leaves = await leavesResponse.json();
            leaves.slice(0, 5).forEach(leave => {
                activities.push({
                    type: 'LEAVE_APPLIED',
                    message: `${leave.employee ? leave.employee.name : 'An employee'} applied for leave`,
                    timestamp: new Date(leave.appliedDate)
                });
            });
        }

        // Get recent duties
        const dutiesResponse = await fetch(`${API_BASE_URL}${ENDPOINTS.admin}/viewassigneddutiesbyadmin`, {
            headers: getAuthHeaders()
        });
        if (dutiesResponse.ok) {
            const duties = await dutiesResponse.json();
            duties.slice(0, 5).forEach(duty => {
                activities.push({
                    type: 'DUTY_ASSIGNED',
                    message: `Duty "${duty.title}" assigned to ${duty.employee ? duty.employee.name : duty.manager ? duty.manager.name : 'someone'}`,
                    timestamp: new Date(duty.assignedDate)
                });
            });
        }

        // Sort activities by timestamp and display
        activities.sort((a, b) => b.timestamp - a.timestamp);
        const activityList = document.getElementById('activityList');
        if (activities.length > 0) {
            activityList.innerHTML = activities.map(activity => `
                <div class="activity-item">
                    <i class="fas ${getActivityIcon(activity.type)}"></i>
                    <div class="activity-details">
                        <p>${activity.message}</p>
                        <small>${formatDateTime(activity.timestamp)}</small>
                    </div>
                </div>
            `).join('');
        } else {
            activityList.innerHTML = '<div class="activity-item"><p>No recent activities</p></div>';
        }
    } catch (error) {
        // Silently handle errors without showing popups
        console.error('Error loading recent activities:', error);
        const activityList = document.getElementById('activityList');
        activityList.innerHTML = '<div class="activity-item"><p>No recent activities</p></div>';
    }
}

// Fetch and display dashboard stats
async function loadDashboardStats() {
    try {
        // Fetch total employees
        const empRes = await fetch(`${API_BASE_URL}${ENDPOINTS.admin}/viewallemployees`, { headers: getAuthHeaders() });
        const employees = await empRes.json();
        document.getElementById('totalEmployees').textContent = employees.length;

        // Fetch total managers
        const mgrRes = await fetch(`${API_BASE_URL}${ENDPOINTS.admin}/viewallmanagers`, { headers: getAuthHeaders() });
        const managers = await mgrRes.json();
        document.getElementById('totalManagers').textContent = managers.length;
    } catch (error) {
        document.getElementById('totalEmployees').textContent = '0';
        document.getElementById('totalManagers').textContent = '0';
    }
}

// Employee Management
async function loadEmployees() {
    try {
        const response = await fetch(`${API_BASE_URL}${ENDPOINTS.admin}/viewallemployees`, {
            headers: getAuthHeaders()
        });
        if (!response.ok) {
            const text = await response.text();
            console.error('Failed to load employees:', text);
            showError(text || 'Failed to load employees');
            const tbody = document.getElementById('employeeTableBody');
            tbody.innerHTML = '<tr><td colspan="7" class="text-center">Failed to load employees</td></tr>';
            return;
        }
        const employees = await response.json();
        const tbody = document.getElementById('employeeTableBody');
        tbody.innerHTML = employees.map(employee => `
            <tr>
                <td>${employee.id}</td>
                <td>${employee.name}</td>
                <td>${employee.department}</td>
                <td>${employee.designation}</td>
                <td>${employee.email}</td>
                <td>
                    <span class="status-badge ${employee.accountstatus ? employee.accountstatus.toLowerCase() : ''}">
                        ${employee.accountstatus || ''}
                    </span>
                </td>
                <td>
                    <button onclick="editEmployee('${employee.id}')" class="btn-icon"><i class="fas fa-edit"></i></button>
                    <button onclick="deleteEmployee('${employee.id}')" class="btn-icon"><i class="fas fa-trash"></i></button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        const tbody = document.getElementById('employeeTableBody');
        tbody.innerHTML = '<tr><td colspan="7" class="text-center">Failed to load employees</td></tr>';
        showError('Failed to load employees: ' + error);
    }
}

window.editEmployee = async function(id) {
    // Implement edit logic or open modal for editing
    alert('Edit Employee: ' + id);
}
window.deleteEmployee = async function(id) {
    if (!confirm('Are you sure you want to delete this employee?')) return;
    try {
        await fetch(`${API_BASE_URL}${ENDPOINTS.admin}/deleteemployee?eid=${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        loadEmployees();
        loadDashboardStats();
        showSuccess('Employee deleted successfully');
    } catch (error) {
        showError('Failed to delete employee');
    }
}

// Manager Management
async function loadManagers() {
    try {
        const response = await fetch(`${API_BASE_URL}${ENDPOINTS.admin}/viewallmanagers`, {
            headers: getAuthHeaders()
        });
        const managers = await response.json();
        const tbody = document.getElementById('managerTableBody');
        tbody.innerHTML = managers.map(manager => `
            <tr>
                <td>${manager.id}</td>
                <td>${manager.name}</td>
                <td>${manager.department}</td>
                <td>${manager.email}</td>
                <td>
                    <button onclick="editManager('${manager.id}')" class="btn-icon"><i class="fas fa-edit"></i></button>
                    <button onclick="deleteManager('${manager.id}')" class="btn-icon"><i class="fas fa-trash"></i></button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        const tbody = document.getElementById('managerTableBody');
        tbody.innerHTML = '<tr><td colspan="5" class="text-center">Failed to load managers</td></tr>';
    }
}

window.editManager = async function(id) {
    // Implement edit logic or open modal for editing
    alert('Edit Manager: ' + id);
}
window.deleteManager = async function(id) {
    if (!confirm('Are you sure you want to delete this manager?')) return;
    try {
        await fetch(`${API_BASE_URL}${ENDPOINTS.admin}/deletemanager?mid=${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        loadManagers();
        loadDashboardStats();
        showSuccess('Manager deleted successfully');
    } catch (error) {
        showError('Failed to delete manager');
    }
}

// Leave Applications
async function loadLeaveApplications() {
    try {
        const response = await fetch(`${API_BASE_URL}${ENDPOINTS.admin}/viewallleaveApplications`, {
            headers: getAuthHeaders()
        });
        const leaves = await response.json();
        
        const tbody = document.getElementById('leaveTableBody');
        tbody.innerHTML = leaves.map(leave => `
            <tr>
                <td>${leave.employee ? leave.employee.name : 'N/A'}</td>
                <td>${new Date(leave.startDate).toLocaleDateString()}</td>
                <td>${new Date(leave.endDate).toLocaleDateString()}</td>
                <td>${leave.reason}</td>
                <td>
                    <span class="status-badge ${leave.status.toLowerCase()}">
                        ${leave.status}
                    </span>
                </td>
                <td>
                    <button onclick="updateLeaveStatus(${leave.id}, 'APPROVED')" class="btn-icon">
                        <i class="fas fa-check"></i>
                    </button>
                    <button onclick="updateLeaveStatus(${leave.id}, 'REJECTED')" class="btn-icon">
                        <i class="fas fa-times"></i>
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        showError('Failed to load leave applications');
    }
}

async function updateLeaveStatus(leaveId, status) {
    try {
        const response = await fetch(`${API_BASE_URL}${ENDPOINTS.admin}/updateleavestatus?leaveid=${leaveId}&status=${status}`, {
            method: 'PUT',
            headers: getAuthHeaders()
        });
        
        if (response.ok) {
            loadLeaveApplications();
            showSuccess('Leave status updated successfully');
        } else {
            const error = await response.json();
            showError(error.message);
        }
    } catch (error) {
        showError('Failed to update leave status');
    }
}

// Duty Management
async function loadDuties() {
    try {
        const userData = JSON.parse(localStorage.getItem('userData'));
        const adminId = userData && userData.id;
        const response = await fetch(`${API_BASE_URL}${ENDPOINTS.admin}/viewassigneddutiesbyadmin?aid=${adminId}`, {
            headers: getAuthHeaders()
        });
        if (!response.ok) {
            const text = await response.text();
            console.error('Failed to load duties:', text);
            showError(text || 'Failed to load duties');
            const tbody = document.getElementById('dutyTableBody');
            tbody.innerHTML = '<tr><td colspan="5" class="text-center">Failed to load duties</td></tr>';
            return;
        }
        const duties = await response.json();
        const tbody = document.getElementById('dutyTableBody');
        if (duties && duties.length > 0) {
            tbody.innerHTML = duties.map(duty => `
                <tr>
                    <td>${duty.employee ? duty.employee.name : duty.manager ? duty.manager.name : 'N/A'}</td>
                    <td>${duty.title}</td>
                    <td>${duty.description}</td>
                    <td>${duty.assignedDate ? new Date(duty.assignedDate).toLocaleDateString() : ''}</td>
                    <td>
                        <button onclick="editDuty(${duty.id})" class="btn-icon">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button onclick="deleteDuty(${duty.id})" class="btn-icon">
                            <i class="fas fa-trash"></i>
                        </button>
                    </td>
                </tr>
            `).join('');
        } else {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center">No duties assigned yet</td></tr>';
        }
    } catch (error) {
        const tbody = document.getElementById('dutyTableBody');
        tbody.innerHTML = '<tr><td colspan="5" class="text-center">Failed to load duties</td></tr>';
        showError('Failed to load duties: ' + error);
    }
}

async function loadAssignees() {
    const assigneeType = document.getElementById('assigneeType').value;
    const assigneeSelect = document.getElementById('assigneeId');
    assigneeSelect.innerHTML = '<option value="">Select Assignee</option>';
    
    if (!assigneeType) return;
    
    try {
        const endpoint = assigneeType === 'employee' 
            ? `${API_BASE_URL}${ENDPOINTS.admin}/viewallemployees`
            : `${API_BASE_URL}${ENDPOINTS.admin}/viewallmanagers`;
            
        const response = await fetch(endpoint, {
            headers: getAuthHeaders()
        });
        
        if (response.ok) {
            const assignees = await response.json();
            if (assignees && assignees.length > 0) {
                assigneeSelect.innerHTML += assignees.map(assignee => `
                    <option value="${assignee.id}">${assignee.name}</option>
                `).join('');
            } else {
                assigneeSelect.innerHTML = '<option value="">No assignees available</option>';
            }
        }
    } catch (error) {
        assigneeSelect.innerHTML = '<option value="">No assignees available</option>';
    }
}

async function handleDutySubmit(e) {
    e.preventDefault();
    const formData = new FormData(e.target);
    const assigneeType = formData.get('assigneeType');
    const assigneeId = formData.get('assigneeId');
    
    const dutyData = {
        title: formData.get('title'),
        description: formData.get('description'),
        assignedDate: new Date().toISOString()
    };
    
    try {
        const endpoint = assigneeType === 'employee'
            ? `${API_BASE_URL}${ENDPOINTS.admin}/assigndutytoemployee`
            : `${API_BASE_URL}${ENDPOINTS.admin}/assigndutytomanager`;
            
        const response = await fetch(endpoint, {
            method: 'PUT',
            headers: {
                ...getAuthHeaders(),
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                ...dutyData,
                assigneeId: assigneeId,
                assigneeType: assigneeType
            })
        });
        
        if (response.ok) {
            closeModal('dutyModal');
            loadDuties();
            showSuccess('Duty assigned successfully');
        } else {
            const error = await response.json();
            showError(error.message || 'Failed to assign duty');
        }
    } catch (error) {
        showError('Failed to assign duty');
    }
}

// Utility Functions
function getAuthHeaders() {
    const token = localStorage.getItem('token');
    return {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
    };
}

function getActivityIcon(type) {
    switch(type) {
        case 'EMPLOYEE_ADDED': return 'fa-user-plus';
        case 'LEAVE_APPLIED': return 'fa-calendar-plus';
        case 'DUTY_ASSIGNED': return 'fa-tasks';
        case 'STATUS_UPDATED': return 'fa-sync';
        default: return 'fa-info-circle';
    }
}

function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'flex';
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'none';
        // Reset form if exists
        const form = modal.querySelector('form');
        if (form) {
            form.reset();
        }
    }
}

function showSuccess(message) {
    // Implement success notification
    alert(message); // Replace with better UI notification
}

function showError(message) {
    // Implement error notification
    alert(message); // Replace with better UI notification
}

// Close modal when clicking outside
window.onclick = function(event) {
    if (event.target.classList.contains('modal')) {
        event.target.style.display = 'none';
        // Reset form if exists
        const form = event.target.querySelector('form');
        if (form) {
            form.reset();
        }
    }
}

function showPage(page) {
    // Hide all content sections
    document.querySelectorAll('.content').forEach(section => section.classList.add('hidden'));
    // Show the selected section
    const section = document.getElementById(page);
    if (section) section.classList.remove('hidden');

    // Load data for the selected page
    if (page === 'employees') loadEmployees();
    if (page === 'managers') loadManagers();
    if (page === 'duties') loadDuties();
    if (page === 'leaves') loadLeaveApplications();
    // Add more as needed
}

function handleLogout() {
    localStorage.clear();
    window.location.href = 'login.html';
}

function initializeDashboard() {
    loadDashboardStats();
    loadEmployees();
    loadManagers();
    loadDuties();
    loadLeaveApplications();
    // Add more as needed
} 