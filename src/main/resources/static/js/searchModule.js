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
            throw new Error(err.error + ":" + error.message);
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

    // Create category filter UI
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

    async function handleSearch(opts = {}) {
        // opts: { page, query, categories }
        let keyword = opts.query !== undefined ? opts.query : searchInput.value;
        let selectedCategories = opts.categories !== undefined ? opts.categories : getSelectedCategories();
        let page = opts.page !== undefined ? opts.page : 0;

        // Build endpoint with page
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
