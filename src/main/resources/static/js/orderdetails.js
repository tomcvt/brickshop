let orderId = null;
document.addEventListener('DOMContentLoaded', async () => {
    orderId = document.getElementById('orderId').getAttribute('data-order-id');
    if (!orderId) {
        showError('Invalid order session.');
        return;
    }
    await loadOrder(orderId);
});

async function loadOrder(orderId) {
    try {
        const res = await fetch(`/api/user/orders/${orderId}`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });
        /*
        if (res.status === 401 || res.status === 403 || res.status === 404) {
            window.location.href = '/userpanel';
            return;
        }*/

        if (!res.ok) {
            const error = await res.json();
            throw Error(error.message);
        }

        const order = await res.json();
        console.log(order);
        renderOrder(order);
        await getAndShowCart(order.cartId);
    } catch (err) {
        showError('Failed to load order: ' + err.message);
    }
}

function renderOrder(order) {
    const container = document.getElementById('order-container');
    container.innerHTML = '';

    container.appendChild(makeField('Order ID', order.orderId));
    container.appendChild(makeField('User', order.username));
    container.appendChild(makeField('Cart ID', order.cartId));
    container.appendChild(makeField('Shipping Address', order.shippingAddressString));
    container.appendChild(makeField('Status', order.status));
    container.appendChild(makeField('Total Amount', `$${order.totalAmount}`));
    container.appendChild(makeField('Payment Method', order.paymentMethod));
    container.appendChild(makeField('Payment Status', order.paymentStatus));
}

function makeField(label, value) {
    const div = document.createElement('div');
    div.className = 'order-field';

    const strong = document.createElement('strong');
    strong.innerText = label + ':';

    const span = document.createElement('span');
    span.innerText = value ?? '-';

    div.appendChild(strong);
    div.appendChild(span);

    return div;
}

function showError(msg) {
    document.getElementById('order-container').innerHTML = '';
    document.getElementById('order-error').innerText = msg;
}

async function getAndShowCart(cartId) {
    try {
        const response = await fetch(`/api/user/carts/${cartId}`);
        console.log(response);
        if (!response.ok) {
            const errObj = await response.json();
            throw Error(errObj.message);
        }
        const cart = await response.json();
        console.log(cart);
        renderCart(cart);
    } catch (err) {
        console.error('Failed to load cart', err);
    }
}

function renderCart(cart) {
    const cartItemsDiv = document.getElementById('cart-items');
    cartItemsDiv.innerHTML = ''; // clear old content

    if (cart.items.length === 0) {
        cartItemsDiv.innerText = 'Your cart is empty.';
        document.getElementById('cart-total').innerText = '';
        return;
    }

    cart.items.forEach(item => {
        const itemDiv = document.createElement('div');
        itemDiv.className = 'cart-row';

        // Product name
        const nameLink = document.createElement('a');
        nameLink.href = `/products/${item.publicId}`;
        nameLink.innerText = item.productName;
        nameLink.target = '_blank';

        // Quantity
        const qtySpan = document.createElement('span');
        qtySpan.className = 'cart-cell';
        qtySpan.innerText = `Qty: ${item.quantity}`;

        // Price
        const priceSpan = document.createElement('span');
        priceSpan.className = 'cart-cell';
        priceSpan.innerText = `Price: $${item.price}`;

        // Subtotal
        const subtotalSpan = document.createElement('span');
        subtotalSpan.className = 'cart-cell';
        const subtotalValue = (item.price * item.quantity).toFixed(2);
        subtotalSpan.innerText = `Subtotal: $${subtotalValue}`;

        // Append cells
        itemDiv.appendChild(nameLink);
        itemDiv.appendChild(qtySpan);
        itemDiv.appendChild(priceSpan);
        itemDiv.appendChild(subtotalSpan);

        cartItemsDiv.appendChild(itemDiv);
    });

    document.getElementById('cart-total').innerText =
        `Total: $${cart.totalPrice}`;
}