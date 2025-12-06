import { initUserSearchModule } from '/js/pageUsersModule.js';

let currentPage = 0;
let pageSize = 10;
let username = '';
let email = '';
let role = '';
let enabled = '';
const endpoint = '/api/superuser/users/search';

function renderUsers(simplePage) {
    const listDiv = document.getElementById('usersList');
    listDiv.innerHTML = '';
    if (!simplePage || !simplePage.content || simplePage.content.length === 0) {
        listDiv.innerHTML = '<div class="alert alert-error">No users found.</div>';
        return;
    }
    simplePage.content.forEach(user => {
        const row = document.createElement('div');
        row.className = 'user-row field';
        row.style.display = 'flex';
        row.style.width = '100%';
        row.innerHTML = `
            <div class="user-cell username">${user.username ?? ''}</div>
            <div class="user-cell email">${user.email ?? ''}</div>
            <div class="user-cell role">${user.role ?? ''}</div>
            <div class="user-cell enabled${user.enabled ? '' : ' disabled'}">${user.enabled ? 'Enabled' : 'Disabled'}</div>
            <div class="user-cell modify-btn-cell">
                <button class="btn btn-small" onclick="alert('Modify function to be implemented')">Modify</button>
            </div>
        `;
        listDiv.appendChild(row);
    });
}

document.addEventListener('DOMContentLoaded', () => {
    // Header filter logic
    const usernameInput = document.getElementById('filter-username');
    const emailInput = document.getElementById('filter-email');
    const roleSelect = document.getElementById('filter-role');
    const enabledSelect = document.getElementById('filter-enabled');
    const searchBtn = document.getElementById('searchBtn');

    usernameInput.addEventListener('input', e => { username = e.target.value; });
    emailInput.addEventListener('input', e => { email = e.target.value; });
    roleSelect.addEventListener('change', e => { role = e.target.value; });
    enabledSelect.addEventListener('change', e => { enabled = e.target.value; });

    initUserSearchModule(
        (results, opts) => {
            currentPage = opts.page;
            pageSize = opts.size;
            username = opts.username;
            email = opts.email;
            role = opts.role;
            enabled = opts.enabled;
            renderUsers(results);
        },
        {
            navBarIds: ['usersNavBar'],
            getCurrentPage: () => currentPage,
            getUsername: () => username,
            getEmail: () => email,
            getRole: () => role,
            getEnabled: () => enabled,
            getSize: () => pageSize,
            getEndpoint: () => endpoint,
            searchButtonId: 'searchBtn'
        }
    );
});
