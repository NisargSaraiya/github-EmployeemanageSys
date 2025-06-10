// API endpoints
const API_BASE_URL = '';
const ENDPOINTS = {
    auth: `${API_BASE_URL}/auth`,
    employee: `${API_BASE_URL}/employee`,
    manager: `${API_BASE_URL}/manager`,
    admin: `${API_BASE_URL}/admin`
};

// DOM Elements
const searchInput = document.querySelector('.search-bar input');
const statsContainer = document.querySelector('.stats-container');
const activityList = document.querySelector('.activity-list');

// Fetch and display employee statistics
async function fetchEmployeeStats() {
    try {
        const response = await fetch(`${ENDPOINTS.admin}/employeecount`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        const employeeCount = await response.json();
        
        const managerResponse = await fetch(`${ENDPOINTS.admin}/managercount`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        const managerCount = await managerResponse.json();
        
        updateStatsDisplay({
            totalEmployees: employeeCount,
            totalDepartments: managerCount,
            newHires: 0, // You might want to implement this endpoint
            growthRate: 0 // You might want to implement this endpoint
        });
    } catch (error) {
        console.error('Error fetching stats:', error);
    }
}

// Update statistics display
function updateStatsDisplay(stats) {
    const statCards = statsContainer.querySelectorAll('.stat-card');
    statCards[0].querySelector('p').textContent = stats.totalEmployees;
    statCards[1].querySelector('p').textContent = stats.totalDepartments;
    statCards[2].querySelector('p').textContent = stats.newHires;
    statCards[3].querySelector('p').textContent = stats.growthRate + '%';
}

// Fetch and display recent activities
async function fetchRecentActivities() {
    try {
        const response = await fetch(`${ENDPOINTS.admin}/viewallleaveApplications`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        const leaves = await response.json();
        updateActivitiesDisplay(leaves.map(leave => ({
            type: 'leave',
            title: 'Leave Application',
            description: `${leave.employee.name} applied for leave`,
            timestamp: leave.createdAt
        })));
    } catch (error) {
        console.error('Error fetching recent activities:', error);
    }
}

// Update activities display
function updateActivitiesDisplay(activities) {
    activityList.innerHTML = activities.map(activity => `
        <div class="activity-item">
            <div class="activity-icon">
                <i class="fas ${getActivityIcon(activity.type)}"></i>
            </div>
            <div class="activity-details">
                <h4>${activity.title}</h4>
                <p>${activity.description}</p>
                <span class="time">${formatTimeAgo(activity.timestamp)}</span>
            </div>
        </div>
    `).join('');
}

// Get appropriate icon for activity type
function getActivityIcon(type) {
    const icons = {
        'leave': 'fa-calendar-alt',
        'duty': 'fa-tasks',
        'employee': 'fa-user-plus',
        'manager': 'fa-user-tie'
    };
    return icons[type] || 'fa-info-circle';
}

// Format timestamp to "time ago" format
function formatTimeAgo(timestamp) {
    const now = new Date();
    const past = new Date(timestamp);
    const diffInSeconds = Math.floor((now - past) / 1000);

    if (diffInSeconds < 60) return 'just now';
    if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)} minutes ago`;
    if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)} hours ago`;
    return `${Math.floor(diffInSeconds / 86400)} days ago`;
}

// Search functionality
searchInput.addEventListener('input', debounce(async (e) => {
    const searchTerm = e.target.value.trim();
    if (searchTerm.length < 2) return;

    try {
        const response = await fetch(`${ENDPOINTS.employee}/viewprofile?empid=${searchTerm}`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        const employee = await response.json();
        // Handle search results (implement based on your UI requirements)
        console.log('Search results:', employee);
    } catch (error) {
        console.error('Error searching employees:', error);
    }
}, 300));

// Debounce function to limit API calls
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

// Navigation handling
document.querySelectorAll('.nav-links a').forEach(link => {
    link.addEventListener('click', (e) => {
        e.preventDefault();
        const target = e.target.getAttribute('href').substring(1);
        navigateTo(target);
    });
});

// Navigation function
function navigateTo(section) {
    // Remove active class from all links
    document.querySelectorAll('.nav-links li').forEach(li => li.classList.remove('active'));
    // Add active class to clicked link
    document.querySelector(`a[href="#${section}"]`).parentElement.classList.add('active');
    
    // Handle navigation (implement based on your requirements)
    console.log(`Navigating to ${section}`);
}

// Initialize the dashboard
async function initializeDashboard() {
    await Promise.all([
        fetchEmployeeStats(),
        fetchRecentActivities()
    ]);
}

// Start the application
document.addEventListener('DOMContentLoaded', initializeDashboard);

// Common utility functions
function showSuccess(message) {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = 'notification success';
    notification.innerHTML = `
        <i class="fas fa-check-circle"></i>
        <span>${message}</span>
    `;
    
    // Add to document
    document.body.appendChild(notification);
    
    // Remove after 3 seconds
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

function showError(message) {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = 'notification error';
    notification.innerHTML = `
        <i class="fas fa-exclamation-circle"></i>
        <span>${message}</span>
    `;
    
    // Add to document
    document.body.appendChild(notification);
    
    // Remove after 3 seconds
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

function formatDate(date) {
    return new Date(date).toLocaleDateString();
}

function formatDateTime(date) {
    return new Date(date).toLocaleString();
}

// Add notification styles
const style = document.createElement('style');
style.textContent = `
    .notification {
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        border-radius: 5px;
        display: flex;
        align-items: center;
        gap: 10px;
        animation: slideIn 0.3s ease-out;
        z-index: 1000;
    }

    .notification.success {
        background-color: #2ecc71;
        color: white;
    }

    .notification.error {
        background-color: #e74c3c;
        color: white;
    }

    @keyframes slideIn {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
`;
document.head.appendChild(style); 