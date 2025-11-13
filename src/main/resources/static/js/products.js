
import * as Cart from './sidebar-cart.js';
import { initSearchBar } from './searchModule.js';

let currentPage = 0;
let lastPage = false;
let firstPage = true;
let lastQuery = '';
let lastCategories = [];
let lastResults = null;

document.addEventListener('DOMContentLoaded', function () {
    initSearchBar((results, opts = {}) => {
        // opts: { page, query, categories }
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
        // fallback fetch
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

    // Update navigation panels (top and bottom)
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
    img.src = item.thumbnailUrl ? `outsideimages/${item.thumbnailUrl}` : 'outsideimages/no-image.jpg';
    img.alt = item.name;

    const title = document.createElement('h2');
    title.textContent = item.name;

    const priceAndStock = document.createElement('p');
    priceAndStock.textContent = `Price: ${item.price} zł    Stock: ${item.stock}`;

    const button = document.createElement('button');
    button.textContent = 'View Product';

    const addToCartBtn = document.createElement('button');
    addToCartBtn.classList.add('btn', 'btn-primary');
    addToCartBtn.textContent = 'Add to Cart';

    button.addEventListener('click', () => {
        window.location.href = `/products/${item.publicId}`;
    });

    card.appendChild(img);
    card.appendChild(title);
    card.appendChild(priceAndStock);
    card.appendChild(button);
    card.appendChild(addToCartBtn);

    addToCartBtn.addEventListener('click', () => {
        Cart.addProductByPublicIdToCart(item.publicId);
    });

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

Cart.loadAndShowCart();