// searchModule.js
let onResultReference = null;

export async function fetchCategories() {
    try {
        const response = await fetch('/api/products/categories', {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });
        if (!response.ok) {
            const err = await response.json();
            throw Error(err.error + ":" + err.message);
        }
        return await response.json(); // array of strings
    } catch (error) {
        console.error('Category fetch failed:', error);
        return [];
    }
}

export async function fetchData(keyword = '', categories = []) {
    let endpoint = '/api/products/summaries';
    const params = [];

    if (keyword.trim() !== '') {
        params.push(`query=${encodeURIComponent(keyword)}`);
    }
    if (categories.length > 0) {
        categories.forEach(cat => {
            params.push(`category=${encodeURIComponent(cat)}`);
        });
    }
    if (params.length > 0) {
        endpoint += '?' + params.join('&');
    }

    try {
        const response = await fetch(endpoint, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });

        if (!response.ok) {
            const err = await response.json();
            throw new Error(err.error + ":" + err.message);
        }

        const data = await response.json();
        return data; // SimplePage<ProductSummaryDto>
    } catch (error) {
        console.error('Fetch failed:', error);
        return null;
    }
}

export async function initSearchBar(onResults, config = {}) {
    const searchInput = document.getElementById('searchKeyword');
    const searchButton = document.getElementById('searchButton');
    const searchBarContainer = document.getElementById('searchBarContainer') || searchInput.parentElement;
    const debounceDelay = config.debounceDelay || 300;

    // --- Category filter UI (unchanged) ---
    let categoryContainer = document.getElementById('categoryFilter');
    if (!categoryContainer) {
        categoryContainer = document.createElement('div');
        categoryContainer.id = 'categoryFilter';
        categoryContainer.style.display = 'inline-block';
        categoryContainer.style.verticalAlign = 'top';
        categoryContainer.style.marginRight = '16px';
        categoryContainer.style.maxWidth = '200px';
        categoryContainer.style.position = 'relative';
        searchBarContainer.insertBefore(categoryContainer, searchInput);
    }

    // Expand/collapse logic
    const expander = document.createElement('button');
    expander.textContent = 'Categories ▼';
    expander.className = 'btn btn-small';
    expander.style.width = '100%';
    expander.style.marginBottom = '6px';
    categoryContainer.appendChild(expander);

    const checkboxList = document.createElement('div');
    checkboxList.style.display = 'none';
    checkboxList.style.flexDirection = 'column';
    checkboxList.style.background = '#fff';
    checkboxList.style.border = '1px solid #ddd';
    checkboxList.style.borderRadius = '5px';
    checkboxList.style.padding = '8px';
    checkboxList.style.position = 'absolute';
    checkboxList.style.zIndex = '10';
    checkboxList.style.width = '100%';
    categoryContainer.appendChild(checkboxList);

    expander.addEventListener('click', () => {
        checkboxList.style.display = checkboxList.style.display === 'none' ? 'flex' : 'none';
    });

    document.addEventListener('click', (e) => {
        if (!categoryContainer.contains(e.target)) {
            checkboxList.style.display = 'none';
        }
    });

    // Populate categories
    const categories = await fetchCategories();
    categories.forEach(cat => {
        const label = document.createElement('label');
        label.style.display = 'flex';
        label.style.alignItems = 'center';
        label.style.marginBottom = '4px';
        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.value = cat;
        checkbox.style.marginRight = '8px';
        label.appendChild(checkbox);
        label.appendChild(document.createTextNode(cat));
        checkboxList.appendChild(label);
    });

    function getSelectedCategories() {
        return Array.from(checkboxList.querySelectorAll('input[type=checkbox]:checked'))
            .map(cb => cb.value);
    }

    // --- Suggestions popup logic ---
    let suggestionPopup = document.getElementById('searchSuggestionsPopup');
    if (!suggestionPopup) {
        suggestionPopup = document.createElement('div');
        suggestionPopup.id = 'searchSuggestionsPopup';
        suggestionPopup.style.position = 'absolute';
        suggestionPopup.style.background = '#fff';
        suggestionPopup.style.border = '1px solid #ccc';
        suggestionPopup.style.borderRadius = '4px';
        suggestionPopup.style.boxShadow = '0 2px 8px rgba(0,0,0,0.08)';
        suggestionPopup.style.display = 'none';
        suggestionPopup.style.minWidth = searchInput.offsetWidth + 'px';
        suggestionPopup.style.zIndex = '100';
        suggestionPopup.style.maxHeight = '220px';
        suggestionPopup.style.overflowY = 'auto';
        suggestionPopup.style.fontSize = '15px';
        suggestionPopup.style.left = searchInput.offsetLeft + 'px';
        suggestionPopup.style.top = (searchInput.offsetTop + searchInput.offsetHeight + 2) + 'px';
        searchBarContainer.appendChild(suggestionPopup);
    }

    function formatPrice(price) {
        if (typeof price === 'number') return price.toFixed(2) + ' zł';
        if (typeof price === 'string') return price + ' zł';
        return '';
    }

    function showSuggestions(suggestions) {
        suggestionPopup.innerHTML = '';
        if (!suggestions || suggestions.length === 0) {
            suggestionPopup.style.display = 'none';
            return;
        }
        suggestions.forEach(item => {
            const row = document.createElement('div');
            row.style.display = 'flex';
            row.style.justifyContent = 'space-between';
            row.style.alignItems = 'center';
            row.style.padding = '7px 12px';
            row.style.cursor = 'pointer';
            row.style.borderBottom = '1px solid #f0f0f0';
            row.addEventListener('mouseover', () => row.style.background = '#f5f5f5');
            row.addEventListener('mouseout', () => row.style.background = '#fff');
            row.addEventListener('click', () => {
                window.location.href = '/products/' + item.publicId;
            });
            const nameSpan = document.createElement('span');
            nameSpan.textContent = item.name;
            nameSpan.style.flex = '1';
            nameSpan.style.marginRight = '12px';
            const priceSpan = document.createElement('span');
            priceSpan.textContent = formatPrice(item.price);
            priceSpan.style.whiteSpace = 'nowrap';
            row.appendChild(nameSpan);
            row.appendChild(priceSpan);
            suggestionPopup.appendChild(row);
        });
        suggestionPopup.style.display = 'block';
    }

    function hideSuggestions() {
        suggestionPopup.style.display = 'none';
    }

    // Debounce helper
    function debounce(fn, delay) {
        let timer = null;
        return function(...args) {
            if (timer) clearTimeout(timer);
            timer = setTimeout(() => fn.apply(this, args), delay);
        };
    }

    // Fetch suggestions from API
    async function fetchSuggestions(keyword) {
        if (!keyword || keyword.trim() === '') {
            hideSuggestions();
            return;
        }
        try {
            const response = await fetch(`/api/products/suggestions?keyword=${encodeURIComponent(keyword)}&limit=5`, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' }
            });
            if (!response.ok) {
                hideSuggestions();
                return;
            }
            const data = await response.json();
            showSuggestions(data);
        } catch (e) {
            hideSuggestions();
        }
    }

    const debouncedFetchSuggestions = debounce(fetchSuggestions, debounceDelay);

    // Show suggestions on input
    searchInput.addEventListener('input', (e) => {
        debouncedFetchSuggestions(searchInput.value);
    });

    // Show suggestions on focus/click
    searchInput.addEventListener('focus', () => {
        fetchSuggestions(searchInput.value);
    });
    searchInput.addEventListener('click', () => {
        fetchSuggestions(searchInput.value);
    });

    // Hide suggestions on click outside
    document.addEventListener('mousedown', (e) => {
        if (!suggestionPopup.contains(e.target) && e.target !== searchInput) {
            hideSuggestions();
        }
    });

    // --- Main search logic (unchanged) ---
    async function handleSearch(opts = {}) {
        let keyword = opts.query !== undefined ? opts.query : searchInput.value;
        let selectedCategories = opts.categories !== undefined ? opts.categories : getSelectedCategories();
        let page = opts.page !== undefined ? opts.page : 0;
        let endpoint = '/api/products/summaries';
        const params = [];
        if (keyword.trim() !== '') {
            params.push(`query=${encodeURIComponent(keyword)}`);
        }
        if (selectedCategories.length > 0) {
            selectedCategories.forEach(cat => {
                params.push(`category=${encodeURIComponent(cat)}`);
            });
        }
        if (page > 0) {
            params.push(`page=${page}`);
        }
        if (params.length > 0) {
            endpoint += '?' + params.join('&');
        }
        try {
            const response = await fetch(endpoint, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' }
            });
            if (!response.ok) {
                const err = await response.json();
                throw new Error(err.error + ":" + err.message);
            }
            const data = await response.json();
            if (onResults) onResults(data, { page, query: keyword, categories: selectedCategories });
        } catch (error) {
            console.error('Fetch failed:', error);
            if (onResults) onResults(null, { page, query: keyword, categories: selectedCategories });
        }
    }

    searchButton.addEventListener('click', () => handleSearch());
    searchInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            handleSearch();
        }
    });

    // Navigation buttons
    if (config.nextPage && Array.isArray(config.nextPage)) {
        config.nextPage.forEach(id => {
            const btn = document.getElementById(id);
            if (btn) {
                btn.addEventListener('click', () => {
                    let page = config.getCurrentPage ? config.getCurrentPage() + 1 : 1;
                    let query = config.getQuery ? config.getQuery() : searchInput.value;
                    let categories = config.getCategories ? config.getCategories() : getSelectedCategories();
                    handleSearch({ page, query, categories });
                });
            }
        });
    }
    if (config.previousPage && Array.isArray(config.previousPage)) {
        config.previousPage.forEach(id => {
            const btn = document.getElementById(id);
            if (btn) {
                btn.addEventListener('click', () => {
                    let page = config.getCurrentPage ? Math.max(0, config.getCurrentPage() - 1) : 0;
                    let query = config.getQuery ? config.getQuery() : searchInput.value;
                    let categories = config.getCategories ? config.getCategories() : getSelectedCategories();
                    handleSearch({ page, query, categories });
                });
            }
        });
    }
    const initialUrlCategories = config.getCategories ? config.getCategories() : getSelectedCategories();
    if (initialUrlCategories.length > 0) {
        // Pre-check categories from URL
        const checkboxes = checkboxList.querySelectorAll('input[type=checkbox]');
        checkboxes.forEach(cb => {
            if (initialUrlCategories.includes(cb.value)) {
                cb.checked = true;
            }
        });
    }

    setTimeout(() => {
        handleSearch({ page: 0 , query: '', categories: initialUrlCategories});
    }, 0);
}

//TODO check the logic and do the controller part
