// API Endpoints
const API_BASE_URL = 'http://localhost:2027';
const ENDPOINTS = {
    auth: '/auth',
    manager: '/manager',
    employee: '/employee'
};

// DOM Elements
const navItems = document.querySelectorAll('.nav-item');
const contentSections = document.querySelectorAll('.content');
const logoutBtn = document.getElementById('logoutBtn');
const managerName = document.getElementById('managerName');

// Initialize dashboard
document.addEventListener('DOMContentLoaded', () => {
    // Check authentication
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    // Set manager name
    const userData = JSON.parse(localStorage.getItem('userData'));
    if (userData) {
        managerName.textContent = userData.name;
    }

    // Initialize dashboard data
    initializeDashboard();
    
    // Set up event listeners
    setupEventListeners();
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
                        loadTeamMembers();
                        break;
                    case 'leaves':
                        loadLeaveRequests();
                        break;
                    case 'duties':
                        loadDuties();
                        break;
                    case 'profile':
                        loadProfile();
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

    // Assign Duty
    document.getElementById('assignDutyBtn').addEventListener('click', async () => {
        await populateEmployeeDropdown();
        openModal('dutyModal');
    });

    // Form Submissions
    document.getElementById('dutyForm').addEventListener('submit', async function(e) {
        e.preventDefault();
        const userData = JSON.parse(localStorage.getItem('userData'));
        const managerid = userData && userData.id;
        const formData = new FormData(e.target);
        const dutyData = {
            employeeId: formData.get('employeeId'),
            dutyTitle: formData.get('dutyTitle'),
            dutyDescription: formData.get('dutyDescription'),
            dueDate: formData.get('dueDate'),
            managerid: managerid
        };
        try {
            const response = await fetch(`${API_BASE_URL}${ENDPOINTS.manager}/assigndutytoemployee?empid=${dutyData.employeeId}&managerid=${managerid}&status=ASSIGNED`, {
                method: 'POST',
                headers: {
                    ...getAuthHeaders(),
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    title: dutyData.dutyTitle,
                    description: dutyData.dutyDescription,
                    dueDate: dutyData.dueDate
                })
            });
            const text = await response.text();
            console.log('Assign duty response:', text);
            if (response.ok) {
                closeModal('dutyModal');
                loadDuties();
                showSuccess('Duty assigned successfully');
            } else {
                showError(text || 'Failed to assign duty');
            }
        } catch (error) {
            showError('Failed to assign duty: ' + error);
        }
    });
    document.getElementById('profileForm').addEventListener('submit', handleProfileSubmit);
}

// Dashboard Functions
async function loadDashboardData() {
    try {
        const [teamCount, leaveCount, dutyCount, myLeaveCount] = await Promise.all([
            fetchTeamCount(),
            fetchPendingLeaveCount(),
            fetchActiveDutyCount(),
            fetchMyLeaveCount()
        ]);

        document.getElementById('teamCount').textContent = teamCount;
        document.getElementById('pendingLeaves').textContent = leaveCount;
        document.getElementById('activeDuties').textContent = dutyCount;
        document.getElementById('myLeaves').textContent = myLeaveCount;
    } catch (error) {
        showError('Failed to load dashboard data');
    }
}

async function fetchTeamCount() {
    const response = await fetch(`${API_BASE_URL}${ENDPOINTS.manager}/teamcount`, {
        headers: getAuthHeaders()
    });
    const data = await response.json();
    return data.count;
}

async function fetchPendingLeaveCount() {
    const response = await fetch(`${API_BASE_URL}${ENDPOINTS.manager}/pendingleavecount`, {
        headers: getAuthHeaders()
    });
    const data = await response.json();
    return data.count;
}

async function fetchActiveDutyCount() {
    const response = await fetch(`${API_BASE_URL}${ENDPOINTS.manager}/activedutycount`, {
        headers: getAuthHeaders()
    });
    const data = await response.json();
    return data.count;
}

async function fetchMyLeaveCount() {
    const response = await fetch(`${API_BASE_URL}${ENDPOINTS.manager}/viewownleaves`, {
        headers: getAuthHeaders()
    });
    const data = await response.json();
    return data.length;
}

// Team Management
async function loadTeam() {
    try {
        const userData = JSON.parse(localStorage.getItem('userData'));
        const managerid = userData && userData.id;
        const response = await fetch(`${API_BASE_URL}${ENDPOINTS.manager}/viewallemployees`, {
            headers: getAuthHeaders()
        });
        if (!response.ok) {
            const text = await response.text();
            console.error('Failed to load team members:', text);
            showError(text || 'Failed to load team members');
            return;
        }
        const team = await response.json();
        const tbody = document.getElementById('teamTableBody');
        tbody.innerHTML = team.map(member => `
            <tr>
                <td>${member.id}</td>
                <td>${member.name}</td>
                <td>${member.department}</td>
                <td>${member.designation}</td>
                <td>${member.email}</td>
                <td>
                    <button onclick="editTeamMember('${member.id}')" class="btn-icon"><i class="fas fa-edit"></i></button>
                    <button onclick="deleteTeamMember('${member.id}')" class="btn-icon"><i class="fas fa-trash"></i></button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        showError('Failed to load team members: ' + error);
    }
}

window.editTeamMember = async function(id) {
    alert('Edit Team Member: ' + id);
}
window.deleteTeamMember = async function(id) {
    if (!confirm('Are you sure you want to delete this employee?')) return;
    try {
        await fetch(`${API_BASE_URL}${ENDPOINTS.admin}/deleteemployee?eid=${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        loadTeamMembers();
        showSuccess('Employee deleted successfully');
    } catch (error) {
        showError('Failed to delete employee');
    }
}

function showPage(page) {
    document.querySelectorAll('.content').forEach(section => section.classList.add('hidden'));
    const section = document.getElementById(page);
    if (section) section.classList.remove('hidden');
    if (page === 'team') loadTeam();
    if (page === 'dashboard') loadDashboardStats();
    if (page === 'duties') loadDuties();
    if (page === 'leaves') loadLeaveRequests();
    if (page === 'profile') loadProfile();
}

function initializeDashboard() {
    loadDashboardStats();
    loadTeam();
    loadDuties();
    loadLeaveRequests();
    loadProfile();
}

window.approveLeave = async function(id) {
    if (!confirm('Approve this leave request?')) return;
    try {
        const response = await fetch(`${API_BASE_URL}${ENDPOINTS.manager}/updateleavestatus?leaveid=${id}&status=APPROVED`, {
            method: 'PUT',
            headers: getAuthHeaders()
        });
        loadLeaves();
        showSuccess('Leave approved');
    } catch (error) {
        showError('Failed to approve leave');
    }
}
window.rejectLeave = async function(id) {
    if (!confirm('Reject this leave request?')) return;
    try {
        const response = await fetch(`${API_BASE_URL}${ENDPOINTS.manager}/updateleavestatus?leaveid=${id}&status=REJECTED`, {
            method: 'PUT',
            headers: getAuthHeaders()
        });
        loadLeaves();
        showSuccess('Leave rejected');
    } catch (error) {
        showError('Failed to reject leave');
    }
}

// Populate employee dropdown in Assign Duty form
async function populateEmployeeDropdown() {
    const userData = JSON.parse(localStorage.getItem('userData'));
    const managerid = userData && userData.id;
    const response = await fetch(`${API_BASE_URL}${ENDPOINTS.manager}/viewallemployees`, {
        headers: getAuthHeaders()
    });
    const employees = await response.json();
    const select = document.querySelector('#dutyModal select[name="employeeId"]');
    select.innerHTML = employees.map(emp => `<option value="${emp.id}">${emp.name}</option>`).join('');
}

// Profile Management
async function loadProfile() {
    try {
        const userData = JSON.parse(localStorage.getItem('userData'));
        const managerid = userData && userData.id;
        const response = await fetch(`${API_BASE_URL}${ENDPOINTS.manager}/viewprofile?managerid=${managerid}`, {
            headers: getAuthHeaders()
        });
        if (!response.ok) {
            const text = await response.text();
            console.error('Failed to load profile:', text);
            showError(text || 'Failed to load profile');
            return;
        }
        const manager = await response.json();
        document.getElementById('profileName').value = manager.name || '';
        document.getElementById('profileUsername').value = manager.username || '';
        document.getElementById('profileContact').value = manager.contact || '';
        document.getElementById('profilePassword').value = manager.password || '';
    } catch (error) {
        showError('Failed to load profile: ' + error);
    }
}

async function handleProfileSubmit(e) {
    e.preventDefault();
    const formData = new FormData(e.target);
    const profileData = Object.fromEntries(formData.entries());
    
    try {
        const response = await fetch(`${API_BASE_URL}${ENDPOINTS.manager}/updateprofile`, {
            method: 'PUT',
            headers: {
                ...getAuthHeaders(),
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(profileData)
        });
        
        if (response.ok) {
            showSuccess('Profile updated successfully');
        } else {
            const error = await response.json();
            showError(error.message);
        }
    } catch (error) {
        showError('Failed to update profile');
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
        case 'LEAVE_APPLIED': return 'fa-calendar-plus';
        case 'DUTY_ASSIGNED': return 'fa-tasks';
        case 'STATUS_UPDATED': return 'fa-sync';
        default: return 'fa-info-circle';
    }
}

function openModal(modalId) {
    document.getElementById(modalId).style.display = 'block';
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

function showSuccess(message) {
    showNotification(message, 'success');
}

function showError(message) {
    showNotification(message, 'error');
}

function showNotification(message, type = 'info') {
    // Remove any existing notification
    const existing = document.querySelector('.notification');
    if (existing) existing.remove();

    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.innerHTML = `
        <i class="fas fa-${type === 'error' ? 'exclamation-circle' : 'check-circle'}"></i>
        <span>${message}</span>
        <button class="close-btn" onclick="this.parentElement.remove()">&times;</button>
    `;
    document.body.appendChild(notification);
    setTimeout(() => {
        notification.remove();
    }, 3500);
}

// Close modals when clicking outside
window.onclick = function(event) {
    if (event.target.classList.contains('modal')) {
        event.target.style.display = 'none';
    }
}

// Fetch and display dashboard stats
async function loadDashboardStats() {
    try {
        // Fetch team members
        const teamRes = await fetch(`${API_BASE_URL}${ENDPOINTS.manager}/viewallemployees`, { headers: getAuthHeaders() });
        const team = await teamRes.json();
        document.getElementById('teamCount').textContent = team.length;
        // Fetch duties and leaves if you have endpoints for them
        // document.getElementById('activeDuties').textContent = ...
        // document.getElementById('pendingLeaves').textContent = ...
    } catch (error) {
        document.getElementById('teamCount').textContent = '0';
    }
}

// Team Management
async function loadTeamMembers() {
    try {
        const response = await fetch(`${API_BASE_URL}${ENDPOINTS.manager}/viewallemployees`, {
            headers: getAuthHeaders()
        });
        const employees = await response.json();
        
        const tbody = document.getElementById('teamTableBody');
        tbody.innerHTML = employees.map(emp => `
            <tr>
                <td>${emp.id}</td>
                <td>${emp.name}</td>
                <td>${emp.department}</td>
                <td>${emp.designation}</td>
                <td>${emp.email}</td>
                <td>
                    <span class="status-badge ${emp.accountstatus.toLowerCase()}">
                        ${emp.accountstatus}
                    </span>
                </td>
                <td>
                    <button onclick="deleteTeamMember('${emp.id}')" class="btn-icon"><i class="fas fa-trash"></i></button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        showError('Failed to load team members');
    }
}

// Leave Management
async function loadLeaveRequests() {
    try {
        const userData = JSON.parse(localStorage.getItem('userData'));
        const managerid = userData && userData.id;
        const response = await fetch(`${API_BASE_URL}${ENDPOINTS.manager}/viewownleaves?managerid=${managerid}`, {
            headers: getAuthHeaders()
        });
        const leaves = await response.json();
        
        const tbody = document.getElementById('leaveTableBody');
        tbody.innerHTML = leaves.map(leave => `
            <tr>
                <td>${leave.employee ? leave.employee.name : ''}</td>
                <td>${leave.startDate ? new Date(leave.startDate).toLocaleDateString() : ''}</td>
                <td>${leave.endDate ? new Date(leave.endDate).toLocaleDateString() : ''}</td>
                <td>${leave.reason || ''}</td>
                <td>
                    <span class="status-badge ${leave.status ? leave.status.toLowerCase() : ''}">
                        ${leave.status || ''}
                    </span>
                </td>
                <td>
                    <button onclick="editLeave('${leave.id}')" class="btn-icon"><i class="fas fa-edit"></i></button>
                    <button onclick="deleteLeave('${leave.id}')" class="btn-icon"><i class="fas fa-trash"></i></button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        showError('Failed to load leave requests');
    }
}

async function updateLeaveStatus(leaveId, status) {
    try {
        const response = await fetch(`${API_BASE_URL}${ENDPOINTS.manager}/updateleavestatus/${leaveId}`, {
            method: 'PUT',
            headers: {
                ...getAuthHeaders(),
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ status })
        });
        
        if (response.ok) {
            loadLeaveRequests();
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
        const managerid = userData && userData.id;
        const response = await fetch(`${API_BASE_URL}${ENDPOINTS.manager}/viewassignedduties?managerid=${managerid}`, {
            headers: getAuthHeaders()
        });
        const duties = await response.json();
        const tbody = document.getElementById('dutyTableBody');
        tbody.innerHTML = duties.map(duty => `
            <tr>
                <td>${duty.employee ? duty.employee.id : ''}</td>
                <td>${duty.employee ? duty.employee.name : ''}</td>
                <td>${duty.description || duty.title || ''}</td>
                <td>
                    <button onclick="editDuty('${duty.id}')" class="btn-icon"><i class="fas fa-edit"></i></button>
                    <button onclick="deleteDuty('${duty.id}')" class="btn-icon"><i class="fas fa-trash"></i></button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        showError('Failed to load duties');
    }
}

function showPage(page) {
    document.querySelectorAll('.content').forEach(section => section.classList.add('hidden'));
    const section = document.getElementById(page);
    if (section) section.classList.remove('hidden');
    if (page === 'team') loadTeam();
    if (page === 'dashboard') loadDashboardStats();
    if (page === 'duties') loadDuties();
    if (page === 'leaves') loadLeaveRequests();
    if (page === 'profile') loadProfile();
}

function initializeDashboard() {
    loadDashboardStats();
    loadTeam();
    loadDuties();
    loadLeaveRequests();
    loadProfile();
} 