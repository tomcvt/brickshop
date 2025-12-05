/*
    * Page Users Module
    * This module handles fetching and displaying paginated user search results for superuser.
    * It provides functions to initialize the user search module and fetch specific pages of results.
    * Example usage:
    * initUserSearchModule((results, opts) => {
    *   // Handle displaying results
    *   currentPage = opts.page;
    *   // opts: { page, username, email, role, size }
    * }, {
    * navBarIds: ['topNavBar', 'bottomNavBar'],
    * getCurrentPage: () => currentPage,
    * getUsername: () => username,
    * getEmail: () => email,
    * getRole: () => role,
    * getSize: () => pageSize,
    * getEndpoint: () => endPoint // endpoint to fetch data from
    * searchButtonId: 'searchBtn',
    * });
*/

export async function initUserSearchModule(onResults, config) {
    const navBarIds = config.navBarIds || [];
    let currentPage = 0;
    let lastUsername = config.getUsername ? config.getUsername() : '';
    let lastEmail = config.getEmail ? config.getEmail() : '';
    let lastRole = config.getRole ? config.getRole() : '';
    let lastSize = config.getSize ? config.getSize() : 10;
    let endpoint = config.getEndpoint ? config.getEndpoint() : console.log('getEndpoint function is required in config');
    navBarIds.forEach(id => {
        const navBar = document.getElementById(id);
        if (navBar) {
            navBar.innerHTML = '';
            const prevBtn = document.createElement('button');
            prevBtn.id = `prevPageBtn_${id}`;
            prevBtn.className = 'btn btn-small';
            prevBtn.innerText = '←';
            const nextBtn = document.createElement('button');
            nextBtn.id = `nextPageBtn_${id}`;
            nextBtn.className = 'btn btn-small';
            nextBtn.innerText = '→';
            const pageNumSpan = document.createElement('span');
            pageNumSpan.id = `pageNum_${id}`;
            pageNumSpan.style.margin = '0 10px';
            pageNumSpan.innerText = ' Page 1 ';
            navBar.appendChild(prevBtn);
            navBar.appendChild(pageNumSpan);
            navBar.appendChild(nextBtn);
            prevBtn.onclick = async () => {
                if (currentPage > 0) {
                    currentPage--;
                    lastSize = config.getSize ? config.getSize() : lastSize;
                    const opts = {
                        page: currentPage,
                        username: lastUsername,
                        email: lastEmail,
                        role: lastRole,
                        size: lastSize
                    };
                    const results = await fetchUserPage(endpoint, opts);
                    onResults(results, opts);
                    updateNavBars(results);
                }
            };
            nextBtn.onclick = async () => {
                currentPage++;
                lastSize = config.getSize ? config.getSize() : lastSize;
                const opts = {
                    page: currentPage,
                    username: lastUsername,
                    email: lastEmail,
                    role: lastRole,
                    size: lastSize
                };
                const results = await fetchUserPage(endpoint, opts);
                onResults(results, opts);
                updateNavBars(results);
            };
        }
    });
    function updateNavBars(results) {
        navBarIds.forEach(id => {
            const prevBtn = document.getElementById(`prevPageBtn_${id}`);
            const nextBtn = document.getElementById(`nextPageBtn_${id}`);
            const pageNumSpan = document.getElementById(`pageNum_${id}`);
            if (prevBtn) {
                prevBtn.disabled = currentPage === 0;
            }
            if (nextBtn) {
                nextBtn.disabled = results ? results.last : true;
            }
            if (pageNumSpan) {
                pageNumSpan.innerText = ` Page ${currentPage + 1} `;
            }
        });
    }

    const searchButton = document.getElementById(config.searchButtonId);
    if (searchButton) {
        searchButton.onclick = async () => {
            const username = config.getUsername ? config.getUsername() : '';
            const email = config.getEmail ? config.getEmail() : '';
            const role = config.getRole ? config.getRole() : '';
            const size = config.getSize ? config.getSize() : lastSize;
            const opts = {
                page: 0,
                username: username,
                email: email,
                role: role,
                size: size
            };
            currentPage = 0;
            lastUsername = username;
            lastEmail = email;
            lastRole = role;
            lastSize = size;
            const results = await fetchUserPage(endpoint, opts);
            onResults(results, opts);
            updateNavBars(results);
        };
    }
}

export async function fetchUserPage(endpoint = '', opts = {}) {
    let url = endpoint;
    const page = opts.page || 0;
    const username = opts.username || '';
    const email = opts.email || '';
    const role = opts.role || '';
    const size = opts.size || 10;
    const params = [];
    params.push(`page=${page}`);
    params.push(`size=${size}`);
    if (username.trim() !== '') {
        params.push(`username=${encodeURIComponent(username)}`);
    }
    if (email.trim() !== '') {
        params.push(`email=${encodeURIComponent(email)}`);
    }
    if (role.trim() !== '') {
        params.push(`role=${encodeURIComponent(role)}`);
    }
    if (params.length > 0) {
        url += '?' + params.join('&');
    }
    try {
        const response = await fetch(url, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });
        if (!response.ok) throw new Error(`Error: ${response.status}`);
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Fetch failed:', error);
        return null;
    }
}
