/*
    * Page Orders Module
    * This module handles fetching and displaying paginated order search results for admin.
    * It provides functions to initialize the order search module and fetch specific pages of results.
    * Example usage:
    * initOrderSearchModule((results, opts) => {
    *   // Handle displaying results
    *   currentPage = opts.page;
    *   currentParameter = opts.*parameter*;
    *   // opts: { page, username, orderStatus, paymentMethod, createdBefore, size }
    * }, {
    * navBarIds: ['topNavBar', 'bottomNavBar'],
    * getCurrentPage: () => currentPage,
    * getUsername: () => username,
    * getOrderStatus: () => orderStatus,
    * getPaymentMethod: () => paymentMethod,
    * getCreatedBefore: () => createdBefore,
    * getSize: () => pageSize,
    * getEndpoint: () => endPoint // endpoint to fetch data from
    * searchButtonId: 'searchBtn',
    * });
*/

export async function initOrderSearchModule(onResults, config) {
    const navBarIds = config.navBarIds || [];
    let currentPage = 0;
    let lastUsername = config.getUsername ? config.getUsername() : '';
    let lastOrderStatus = config.getOrderStatus ? config.getOrderStatus() : '';
    let lastPaymentMethod = config.getPaymentMethod ? config.getPaymentMethod() : '';
    let lastCreatedBefore = config.getCreatedBefore ? config.getCreatedBefore() : '';
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
                        orderStatus: lastOrderStatus,
                        paymentMethod: lastPaymentMethod,
                        createdBefore: lastCreatedBefore,
                        size: lastSize
                    };
                    const results = await fetchOrderPage(endpoint, opts);
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
                    orderStatus: lastOrderStatus,
                    paymentMethod: lastPaymentMethod,
                    createdBefore: lastCreatedBefore,
                    size: lastSize
                };
                const results = await fetchOrderPage(endpoint, opts);
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
            const orderStatus = config.getOrderStatus ? config.getOrderStatus() : '';
            const paymentMethod = config.getPaymentMethod ? config.getPaymentMethod() : '';
            const createdBefore = config.getCreatedBefore ? config.getCreatedBefore() : '';
            const size = config.getSize ? config.getSize() : lastSize;
            const opts = {
                page: 0,
                username: username,
                orderStatus: orderStatus,
                paymentMethod: paymentMethod,
                createdBefore: createdBefore,
                size: size
            };
            currentPage = 0;
            lastUsername = username;
            lastOrderStatus = orderStatus;
            lastPaymentMethod = paymentMethod;
            lastCreatedBefore = createdBefore;
            lastSize = size;
            const results = await fetchOrderPage(endpoint, opts);
            onResults(results, opts);
            updateNavBars(results);
        };
    }
}

export async function fetchOrderPage(endpoint = '', opts = {}) {
    let url = endpoint;
    const page = opts.page || 0;
    const username = opts.username || '';
    const orderStatus = opts.orderStatus || '';
    const paymentMethod = opts.paymentMethod || '';
    const createdBefore = opts.createdBefore || '';
    const size = opts.size || 10;
    const params = [];
    params.push(`page=${page}`);
    params.push(`size=${size}`);
    if (username.trim() !== '') {
        params.push(`username=${encodeURIComponent(username)}`);
    }
    if (orderStatus.trim() !== '') {
        params.push(`status=${encodeURIComponent(orderStatus)}`);
    }
    if (paymentMethod.trim() !== '') {
        params.push(`paymentMethod=${encodeURIComponent(paymentMethod)}`);
    }
    if (createdBefore.trim() !== '') {
        params.push(`createdBefore=${encodeURIComponent(createdBefore)}`);
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
