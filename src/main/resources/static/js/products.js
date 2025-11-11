import * as Cart from './sidebar-cart.js';
import { initSearchBar } from './searchModule.js';

document.addEventListener('DOMContentLoaded', function () {
    initSearchBar((results) => {
        loadAndShowProducts(results)
    });
});


async function loadAndShowProducts(products) {
    const container = document.getElementById('productList');
    container.innerHTML = ''; // clear old
    if (products) {
        products.forEach(item => {
            createProductCard(item);
        })
        return;
    }
    try {
        const response = await fetch('/api/products/summaries');
        const items = await response.json();
        console.log(items);


        items.forEach(item => {
            createProductCard(item);
        });
    } catch (err) {
        console.error('Failed to load products', err);
    }
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
    //addToCartBtn.style.marginLeft = '10px';

    button.addEventListener('click', () => {
        // Redirect to dynamic product page
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

loadAndShowProducts();

Cart.loadAndShowCart();