import { initSearchBar } from './searchModule.js';

let currentPage = 0;
let lastPage = false;
let firstPage = true;
let lastQuery = '';
let lastCategories = [];
let lastResults = null;

document.addEventListener('DOMContentLoaded', function () {
    initSearchBar((results, opts = {}) => {
        if (opts.page !== undefined) currentPage = opts.page;
        if (opts.query !== undefined) lastQuery = opts.query;
        if (opts.categories !== undefined) lastCategories = opts.categories;
        loadAndShowProducts(results);
    }, {
        nextPage: ['nextPageBtnTop', 'nextPageBtnBottom'],
        previousPage: ['prevPageBtnTop', 'prevPageBtnBottom'],
        getCurrentPage: () => currentPage,
        getQuery: () => lastQuery,
        getCategories: () => lastCategories
    });
});

async function loadAndShowProducts(pageObj) {
    const container = document.getElementById('productList');
    container.innerHTML = '';
    let products = [];
    if (pageObj && pageObj.content) {
        products = pageObj.content;
        currentPage = pageObj.number || 0;
        lastPage = pageObj.last;
        firstPage = pageObj.first;
        lastResults = pageObj;
    } else if (Array.isArray(pageObj)) {
        products = pageObj;
    } else {
        try {
            const response = await fetch(`/api/products/summaries?page=${currentPage}`);
            const data = await response.json();
            products = data.content || [];
            currentPage = data.number || 0;
            lastPage = data.last;
            firstPage = data.first;
            lastResults = data;
        } catch (err) {
            console.error('Failed to load products', err);
        }
    }
    updateNavPanel('prevPageBtnTop', 'nextPageBtnTop', 'pageNumTop', currentPage, firstPage, lastPage);
    updateNavPanel('prevPageBtnBottom', 'nextPageBtnBottom', 'pageNumBottom', currentPage, firstPage, lastPage);
    products.forEach(item => {
        createProductCard(item);
    });
}

function createProductCard(item) {
    const container = document.getElementById('productList');
    const card = document.createElement('div');
    card.classList.add('product-card');
    const img = document.createElement('img');
    img.src = item.thumbnailUrl 
        ? `${window.location.origin}/outsideimages/${item.thumbnailUrl}` 
        : `${window.location.origin}/no-image.jpg`;
    img.alt = item.name;
    const title = document.createElement('h2');
    title.textContent = item.name;
    const priceAndStock = document.createElement('p');
    priceAndStock.textContent = `Price: ${item.price} zł    Stock: ${item.stock}`;
    const viewButton = document.createElement('button');
    viewButton.textContent = 'View Product';
    viewButton.addEventListener('click', () => {
        window.location.href = `/products/${item.publicId}`;
    });
    const editButton = document.createElement('button');
    editButton.classList.add('btn', 'btn-secondary');
    editButton.textContent = 'Edit';
    editButton.addEventListener('click', () => {
        window.location.href = `/admin/edit-product/${item.publicId}`;
    });
    card.appendChild(img);
    card.appendChild(title);
    card.appendChild(priceAndStock);
    card.appendChild(viewButton);
    card.appendChild(editButton);
    container.appendChild(card);
}

function updateNavPanel(prevBtnId, nextBtnId, pageNumId, page, first, last) {
    const prevBtn = document.getElementById(prevBtnId);
    const nextBtn = document.getElementById(nextBtnId);
    const pageNum = document.getElementById(pageNumId);
    if (prevBtn) prevBtn.disabled = !!first;
    if (nextBtn) nextBtn.disabled = !!last;
    if (pageNum) pageNum.textContent = ` Page ${page + 1} `;
}
