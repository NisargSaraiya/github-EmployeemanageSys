// API Endpoints
const API_BASE_URL = 'http://localhost:2027';
const ENDPOINTS = {
    auth: '/auth',
    employee: '/employee'
};

// DOM Elements
const navItems = document.querySelectorAll('.nav-item');
const contentSections = document.querySelectorAll('.content');
const logoutBtn = document.getElementById('logoutBtn');
const employeeName = document.getElementById('employeeName');

// Initialize dashboard
document.addEventListener('DOMContentLoaded', () => {
    // Check authentication
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    // Set employee name
    const userData = JSON.parse(localStorage.getItem('userData'));
    if (userData) {
        employeeName.textContent = userData.name;
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
                    case 'profile':
                        loadProfile();
                        break;
                    case 'leaves':
                        loadLeaves();
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

    // Apply Leave
    document.getElementById('applyLeaveBtn').addEventListener('click', () => {
        openModal('leaveModal');
    });

    // Form Submissions
    document.getElementById('leaveForm').addEventListener('submit', handleLeaveSubmit);
    document.getElementById('dutyUpdateForm').addEventListener('submit', handleDutyUpdateSubmit);
    document.getElementById('profileForm').addEventListener('submit', handleProfileSubmit);
}

// Dashboard Functions
async function loadDashboardData() {
    try {
        const [leaveBalance, activeDuties, pendingLeaves, completedDuties] = await Promise.all([
            fetchLeaveBalance(),
            fetchActiveDutyCount(),
            fetchPendingLeaveCount(),
            fetchCompletedDutyCount()
        ]);

        document.getElementById('leaveBalance').textContent = `${leaveBalance} days`;
        document.getElementById('activeDuties').textContent = activeDuties;
        document.getElementById('pendingLeaves').textContent = pendingLeaves;
        document.getElementById('completedDuties').textContent = completedDuties;

        await loadRecentActivities();
    } catch (error) {
        showError('Failed to load dashboard data');
    }
}

async function fetchLeaveBalance() {
    const response = await fetch(`${API_BASE_URL}${ENDPOINTS.employee}/leavebalance`, {
        headers: getAuthHeaders()
    });
    const data = await response.json();
    return data.balance;
}

async function fetchActiveDutyCount() {
    const response = await fetch(`${API_BASE_URL}${ENDPOINTS.employee}/activedutycount`, {
        headers: getAuthHeaders()
    });
    const data = await response.json();
    return data.count;
}

async function fetchPendingLeaveCount() {
    const response = await fetch(`${API_BASE_URL}${ENDPOINTS.employee}/pendingleavecount`, {
        headers: getAuthHeaders()
    });
    const data = await response.json();
    return data.count;
}

async function fetchCompletedDutyCount() {
    const response = await fetch(`${API_BASE_URL}${ENDPOINTS.employee}/completeddutycount`, {
        headers: getAuthHeaders()
    });
    const data = await response.json();
    return data.count;
}

async function loadRecentActivities() {
    try {
        const response = await fetch(`${API_BASE_URL}${ENDPOINTS.employee}/recentactivities`, {
            headers: getAuthHeaders()
        });
        const activities = await response.json();
        
        const activityList = document.getElementById('activityList');
        activityList.innerHTML = activities.map(activity => `
            <div class="activity-item">
                <i class="fas ${getActivityIcon(activity.type)}"></i>
                <div class="activity-details">
                    <p>${activity.description}</p>
                    <small>${new Date(activity.timestamp).toLocaleString()}</small>
                </div>
            </div>
        `).join('');
    } catch (error) {
        showError('Failed to load recent activities');
    }
}

// Profile Management
async function loadProfile() {
    try {
        const userData = JSON.parse(localStorage.getItem('userData'));
        const empid = userData && userData.id;
        const response = await fetch(`${API_BASE_URL}${ENDPOINTS.employee}/viewprofile?empid=${empid}`, {
            headers: getAuthHeaders()
        });
        const profile = await response.json();
        
        // Populate form fields
        document.getElementById('name').value = profile.name;
        document.getElementById('department').value = profile.department;
        document.getElementById('designation').value = profile.designation;
        document.getElementById('email').value = profile.email;
        document.getElementById('contact').value = profile.contact;
    } catch (error) {
        showError('Failed to load profile');
    }
}

async function handleProfileSubmit(e) {
    e.preventDefault();
    const formData = new FormData(e.target);
    const profileData = Object.fromEntries(formData.entries());
    
    try {
        const response = await fetch(`${API_BASE_URL}${ENDPOINTS.employee}/updateprofile`, {
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

// Leave Management
async function loadLeaves() {
    try {
        const userData = JSON.parse(localStorage.getItem('userData'));
        const empid = userData && userData.id;
        const response = await fetch(`${API_BASE_URL}${ENDPOINTS.employee}/viewownleaves?empid=${empid}`, {
            headers: getAuthHeaders()
        });
        const leaves = await response.json();
        
        const tbody = document.getElementById('leaveTableBody');
        tbody.innerHTML = leaves.map(leave => `
            <tr>
                <td>${new Date(leave.startDate).toLocaleDateString()}</td>
                <td>${new Date(leave.endDate).toLocaleDateString()}</td>
                <td>${leave.reason}</td>
                <td>
                    <span class="status-badge ${leave.status.toLowerCase()}">
                        ${leave.status}
                    </span>
                </td>
                <td>
                    ${leave.status === 'PENDING' ? `
                        <button onclick="cancelLeave(${leave.id})" class="btn-icon">
                            <i class="fas fa-times"></i>
                        </button>
                    ` : ''}
                </td>
            </tr>
        `).join('');
    } catch (error) {
        showError('Failed to load leaves');
    }
}

async function handleLeaveSubmit(e) {
    e.preventDefault();
    const formData = new FormData(e.target);
    const leaveData = Object.fromEntries(formData.entries());
    const userData = JSON.parse(localStorage.getItem('userData'));
    const empid = userData && userData.id;
    try {
        const response = await fetch(`${API_BASE_URL}${ENDPOINTS.employee}/applyleave?empid=${empid}`, {
            method: 'POST',
            headers: {
                ...getAuthHeaders(),
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(leaveData)
        });
        if (response.ok) {
            closeModal('leaveModal');
            loadLeaves();
            showSuccess('Leave applied successfully');
        } else {
            const error = await response.json();
            showError(error.message || 'Failed to apply for leave');
        }
    } catch (error) {
        showError('Failed to apply for leave');
    }
}

async function cancelLeave(leaveId) {
    if (!confirm('Are you sure you want to cancel this leave application?')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}${ENDPOINTS.employee}/cancelleave/${leaveId}`, {
            method: 'PUT',
            headers: getAuthHeaders()
        });
        
        if (response.ok) {
            loadLeaves();
            showSuccess('Leave application cancelled successfully');
        } else {
            const error = await response.json();
            showError(error.message);
        }
    } catch (error) {
        showError('Failed to cancel leave application');
    }
}

// Duty Management
async function loadDuties() {
    try {
        const userData = JSON.parse(localStorage.getItem('userData'));
        const empid = userData && userData.id;
        const response = await fetch(`${API_BASE_URL}${ENDPOINTS.employee}/viewduties?empid=${empid}`, {
            headers: getAuthHeaders()
        });
        const duties = await response.json();
        const tbody = document.getElementById('dutyTableBody');
        tbody.innerHTML = duties.map(duty => `
    <tr>
        <td>${duty.description}</td>
        <td>${duty.assignedDate || ''}</td>
        <td>${duty.employeeName || ''}</td>
        <td>
            <button onclick="deleteDuty(${duty.id})" class="btn-icon" title="Delete">
                <i class="fas fa-trash"></i>
            </button>
        </td>
    </tr>
`).join('');
    } catch (error) {
        showError('Failed to load duties');
    }
}

async function updateDutyStatus(dutyId) {
    try {
        const response = await fetch(`${API_BASE_URL}${ENDPOINTS.employee}/viewduty/${dutyId}`, {
            headers: getAuthHeaders()
        });
        const duty = await response.json();
        
        // Populate form
        const form = document.getElementById('dutyUpdateForm');
        form.dataset.dutyId = dutyId;
        form.querySelector('[name="status"]').value = duty.status;
        
        openModal('dutyUpdateModal');
    } catch (error) {
        showError('Failed to load duty details');
    }
}

async function handleDutyUpdateSubmit(e) {
    e.preventDefault();
    const formData = new FormData(e.target);
    const dutyData = Object.fromEntries(formData.entries());
    const dutyId = e.target.dataset.dutyId;
    
    try {
        const response = await fetch(`${API_BASE_URL}${ENDPOINTS.employee}/updateduty/${dutyId}`, {
            method: 'PUT',
            headers: {
                ...getAuthHeaders(),
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(dutyData)
        });
        
        if (response.ok) {
            closeModal('dutyUpdateModal');
            loadDuties();
            showSuccess('Duty status updated successfully');
        } else {
            const error = await response.json();
            showError(error.message);
        }
    } catch (error) {
        showError('Failed to update duty status');
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
        case 'LEAVE_STATUS': return 'fa-calendar-check';
        case 'DUTY_ASSIGNED': return 'fa-tasks';
        case 'DUTY_UPDATED': return 'fa-sync';
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
    // Implement success notification
    alert(message); // Replace with better UI notification
}

function showError(message) {
    // Implement error notification
    alert(message); // Replace with better UI notification
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
        // Fetch duties and leaves if you have endpoints for them
        // document.getElementById('activeDuties').textContent = ...
        // document.getElementById('pendingLeaves').textContent = ...
    } catch (error) {
        // Set to 0 or show error
    }
}

window.editDuty = async function(id) {
    alert('Edit Duty: ' + id);
}

window.deleteDuty = async function(id) {
    if (!confirm('Are you sure you want to delete this duty?')) return;
    try {
        const response = await fetch(`${API_BASE_URL}${ENDPOINTS.employee}/deleteduty/${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        if (response.ok) {
            loadDuties();
            loadDashboardStats && loadDashboardStats();
            showSuccess('Duty deleted successfully');
        } else {
            const error = await response.json();
            showError(error.message || 'Failed to delete duty');
        }
    } catch (error) {
        showError('Failed to delete duty');
    }
}

function showPage(page) {
    document.querySelectorAll('.content').forEach(section => section.classList.add('hidden'));
    const section = document.getElementById(page);
    if (section) section.classList.remove('hidden');
    if (page === 'duties') loadDuties();
    if (page === 'dashboard') loadDashboardStats();
    // Add more as needed
}

function initializeDashboard() {
    loadDashboardStats();
    loadDuties();
    // Add more as needed
} 