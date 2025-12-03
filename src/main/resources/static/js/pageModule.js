
/*
    * Page Module
    * This module handles fetching and displaying paginated search results.
    * It provides functions to initialize the search module and fetch specific pages of results.
    * FOR NOW ITS JUST FOR PAGINATION ON VARIABLE ENDPOINTS
    * It can be extended later to include sorting and filtering options.
    * Example usage:
    * initSearchModule((results, opts) => {
    *   // Handle displaying results
    *   currentPage = opts.page;
    *   currentSize = opts.size;
    *   currentQuery = opts.query;
    *   currentState = opts.state;
    *   // opts: { page, query, state, size }
    * }, {
    * navBarIds: ['topNavBar', 'bottomNavBar'], // IDs of navigation bars (for now empty div to render data in)
    * getCurrentPage: () => currentPage,
    * getQuery: () => currentQuery,
    * getSize: () => pageSize,
    * getState: () => document.getElementById('stateSelect').value,
    * getEndpoint: () => endPoint // endpoint to fetch data from
    * searchButtonId: 'searchBtn', // ID of search button
    * });
*/


export async function initSearchModule(onResults, config) {
    const navBarIds = config.navBarIds || [];
    let currentPage = 0;
    let lastQuery = '';
    let lastState = config.getState ? config.getState() : '';
    let lastSize = config.getSize ? config.getSize() : 5;
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
                    const opts = { page: currentPage, query: lastQuery, state: lastState, size: lastSize };
                    const results = await fetchPage(endpoint, opts);
                    onResults(results, opts);
                    updateNavBars(results);
                }
            };
            nextBtn.onclick = async () => {
                currentPage++;
                lastSize = config.getSize ? config.getSize() : lastSize;
                const opts = { page: currentPage, query: lastQuery, state: lastState, size: lastSize };
                const results = await fetchPage(endpoint, opts);
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
            const query = config.getQuery ? config.getQuery() : '';
            const state = config.getState ? config.getState() : '';
            const size = config.getSize ? config.getSize() : lastSize;
            const opts = { page: 0, query: query, state: state, size: size };
            currentPage = 0;
            lastQuery = query;
            lastState = state;
            lastSize = size;
            const results = await fetchPage(endpoint, opts);
            onResults(results, opts);
            updateNavBars(results);
        };
    }
}


export async function fetchPage(endpoint = '', opts = {}) {
    let url = endpoint;
    const page = opts.page || 0;
    const keyword = opts.query || '';
    const state = opts.state || '';
    const size = opts.size || 5;
    const params = [];
    params.push(`page=${page}`);
    params.push(`size=${size}`);
    if (keyword.trim() !== '') {
        params.push(`query=${encodeURIComponent(keyword)}`);
    }
    //categories not needed for now
    if (state.trim() !== '') {
        params.push(`status=${encodeURIComponent(state)}`);
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