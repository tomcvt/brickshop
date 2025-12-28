import * as SidebarCart from './sidebar-cart.js';


function getProductId() {
    const el = document.getElementById('productPublicId');
    if (el && el.getAttribute('data-product-public-id')) {
        return el.getAttribute('data-product-public-id');
    }
    // Fallback: try to get from URL (e.g. /products/{publicId} or ?id=...)
    const urlMatch = window.location.pathname.match(/([0-9a-fA-F\-]{36})/);
    if (urlMatch) {
        return urlMatch[1];
    }
    const params = new URLSearchParams(window.location.search);
    if (params.has('id')) {
        return params.get('id');
    }
    return null;
}

const productId = getProductId();

function renderCategories(categories) {
    const container = document.getElementById('categories-container');
    container.innerHTML = '';
    if (categories && categories.length > 0) {
        categories.forEach(cat => {
            const btn = document.createElement('button');
            btn.innerText = cat;
            btn.classList.add('btn', 'btn-small', 'btn-tertiary', 'category-btn');
            btn.addEventListener('click', () => {
                window.location.href = `/products?category=${encodeURIComponent(cat)}`;
            });
            container.appendChild(btn);
        });
    }
}

async function loadProduct(productId) {
    const response = await fetch(`/api/products/w-html/${productId}`);
    const product = await response.json();
    console.log('Loaded product:', product);
    document.getElementById('productName').innerText = product.name;
    document.getElementById('productDescription').innerText = product.description;
    document.getElementById('productPrice').innerText = product.price + ' PLN';
    document.getElementById('productStock').innerText = product.stock > 0
        ? product.stock + ' in stock'
        : 'Out of stock';
    document.getElementById('addToCartBtn').disabled = product.stock === 0;
    document.getElementById('productHtmlDescription').innerHTML = product.htmlDescription || '';

    renderCategories(product.categoriesNames);

    const mainImage = document.getElementById('mainImage');
    if (product.imageUrls && product.imageUrls.length > 0) {
        mainImage.src = '/outsideimages/' + product.imageUrls[0];
    } else {
        mainImage.src = '/images/placeholder.jpg';
    }

    const thumbnailContainer = document.getElementById('thumbnailContainer');
    thumbnailContainer.innerHTML = '';
    if (product.imageUrls && product.imageUrls.length > 0) {
        product.imageUrls.forEach(url => {
            const img = document.createElement('img');
            img.src = '/outsideimages/' + url;
            img.addEventListener('click', () => mainImage.src = img.src);
            thumbnailContainer.appendChild(img);
        });
    }

    mainImage.addEventListener('click', () => {
        const lightbox = document.getElementById('lightbox');
        const lightboxImg = document.getElementById('lightboxImg');
        lightboxImg.src = mainImage.src;
        lightbox.style.display = 'flex';
    });

    document.getElementById('lightbox').addEventListener('click', () => {
        document.getElementById('lightbox').style.display = 'none';
    });

    document.getElementById('addToCartBtn').addEventListener('click', () => {SidebarCart.addProductByPublicIdToCart(productId)});
}
loadProduct(productId);

SidebarCart.loadAndShowCart();