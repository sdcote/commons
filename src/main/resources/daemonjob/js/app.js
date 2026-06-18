document.addEventListener('DOMContentLoaded', () => {
    const loginContainer = document.getElementById('login-container');
    const appContainer = document.getElementById('app-container');
    const loginForm = document.getElementById('login-form');
    const loginError = document.getElementById('login-error');
    const logoutBtn = document.getElementById('logout-btn');
    const statusContent = document.getElementById('status-content');

    // Check for existing session token
    if (sessionStorage.getItem('authToken')) {
        showApp();
    }

    // Handle Login
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        try {
            const response = await fetch('/api/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });

            if (response.ok) {
                const data = await response.json();
                sessionStorage.setItem('authToken', data.token); // Store token provided by Java backend
                showApp();
                loginError.textContent = '';
            } else {
                loginError.textContent = 'Invalid credentials.';
            }
        } catch (error) {
            loginError.textContent = 'Connection error.';
        }
    });

    // Handle Logout
    logoutBtn.addEventListener('click', () => {
        sessionStorage.removeItem('authToken');
        hideApp();
    });

    // Handle Control Action
    document.getElementById('gc-btn').addEventListener('click', async () => {
        await authenticatedFetch('/api/control/gc', { method: 'POST' });
        fetchSystemStatus(); // Refresh data after action
    });

    function showApp() {
        loginContainer.classList.add('hidden');
        appContainer.classList.remove('hidden');
        fetchSystemStatus();
    }

    function hideApp() {
        appContainer.classList.add('hidden');
        loginContainer.classList.remove('hidden');
        loginForm.reset();
    }

    // Wrapper for API calls that require authentication
    async function authenticatedFetch(url, options = {}) {
        const token = sessionStorage.getItem('authToken');
        const headers = { ...options.headers, 'Authorization': `Bearer ${token}` };

        const response = await fetch(url, { ...options, headers });
        if (response.status === 401) {
            hideApp(); // Token expired or invalid
            throw new Error('Unauthorized');
        }
        return response;
    }

    // Fetch dynamic data from a Java Responder
    async function fetchSystemStatus() {
        try {
            const response = await authenticatedFetch('/api/status');
            const data = await response.json();
            statusContent.innerHTML = `
                <p>Uptime: ${data.uptime}</p>
                <p>Memory Usage: ${data.memoryUsage} MB</p>
                <p>Active Threads: ${data.threadCount}</p>
            `;
        } catch (error) {
            console.error('Failed to fetch status', error);
        }
    }
});