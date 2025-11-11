import * as SidebarCart from './sidebar-cart.js';

const productId = document.getElementById('productPublicId').getAttribute('data-product-public-id')

async function loadProduct(productId) {
    const response = await fetch(`/api/products/${productId}`);
    const product = await response.json();

    document.getElementById('productName').innerText = product.name;
    document.getElementById('productDescription').innerText = product.description;
    document.getElementById('productPrice').innerText = product.price + ' $';
    document.getElementById('productStock').innerText = product.stock > 0
        ? product.stock + ' in stock'
        : 'Out of stock';
    document.getElementById('addToCartBtn').disabled = product.stock === 0;

    const mainImage = document.getElementById('mainImage');
    mainImage.src = '/outsideimages/' + product.imageUrls[0];

    const thumbnailContainer = document.getElementById('thumbnailContainer');
    thumbnailContainer.innerHTML = '';
    product.imageUrls.forEach(url => {
        const img = document.createElement('img');
        img.src = '/outsideimages/' + url;
        img.addEventListener('click', () => mainImage.src = img.src);
        thumbnailContainer.appendChild(img);
    });

    mainImage.addEventListener('click', () => {
        const lightbox = document.getElementById('lightbox');
        const lightboxImg = document.getElementById('lightboxImg');
        lightboxImg.src = mainImage.src;
        lightbox.style.display = 'flex';
    });

    document.getElementById('lightbox').addEventListener('click', () => {
        document.getElementById('lightbox').style.display = 'none';
    });

    document.getElementById('addToCartBtn').addEventListener('click', () => {SidebarCart.addProductByIdToCart(productId)});
}
loadProduct(productId);

SidebarCart.loadAndShowCart();