export async function loadAndShowCart() {
    try {
        const response = await fetch('/api/cart/cartwithtotal');
        const cart = await response.json();
        console.log(cart);
        const container = document.getElementById('cart-items');
        container.innerHTML = ''; // clear old
        cart.items.forEach(item => {
            createAndAttachRow(item, container);
        });
        const total = document.getElementById('cart-total');
        total.innerText = `Total amount: ${cart.totalPrice}PLN`
    } catch (err) {
        console.error('Failed to load cart', err);
    }
}

export function createAndAttachRow(item, container) {
    const row = document.createElement('div');
    row.classList.add('cart-row');

    const name = document.createElement('span');
    name.classList.add('cart-name');
    name.textContent = item.productName;

    const price = document.createElement('span');
    price.classList.add('cart-price');
    price.textContent = `${item.price} PLN`;

    const qty = document.createElement('span');
    qty.classList.add('cart-qty');
    qty.textContent = `x${item.quantity} pcs.`;

    const removeBtn = document.createElement('button');
    removeBtn.classList.add('cart-remove');
    removeBtn.textContent = '×';

    removeBtn.addEventListener('click', async () => {
        try {
            const res = await fetch(`/api/cart/remove/${item.cartItemId}`, { method: 'POST' });
            if (res.ok) {
                loadAndShowCart();
            }
            else console.error('Failed to remove item, server responded with status:', res.status);
        } catch (err) {
            console.error('Failed to remove item:', err);
            console.log('CartItem ID:', item.cartItemId);
        }
    });

    // append elements to row
    row.appendChild(name);
    row.appendChild(price);
    row.appendChild(qty);
    row.appendChild(removeBtn);

    // append row to container
    container.appendChild(row);
}

export async function addProductByPublicIdToCart(publicId) {
    const cartItemDto = {
        publicId: publicId,
        quantity: 1
    };
    const messageDiv = document.getElementById('cart-message');
    try {
        const response = await fetch('/api/cart/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(cartItemDto)
        });

        if (response.ok) {
            messageDiv.innerText = 'Product added';
            messageDiv.className = 'alert alert-success';
            loadAndShowCart();
        } else {
            const error = await response.text();
            messageDiv.innerText = 'Error: ' + error;
            messageDiv.className = 'alert alert-error';
        }

    } catch (err) {
        messageDiv.innerText = 'Unexpected error';
        messageDiv.className = 'alert alert-error';
    }
}

const checkoutBtn = document.getElementById('checkout-btn');
checkoutBtn.addEventListener('click', () => {
    window.location.href = '/checkout';
});
const cartBtn = document.getElementById('cart-toggle-btn');
const cartPanel = document.getElementById('cart-panel');

cartBtn.addEventListener('click', () => {
    cartPanel.classList.toggle('open');
});