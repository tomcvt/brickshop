let checkoutSession = null;
let addresses = [];
const result = document.getElementById('result');

// Call /api/checkout/create

/*
document.getElementById('createSessionBtn').addEventListener('click', async () => {
    try {
        const res = await fetch('/api/checkout/create', {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });
        if (!res.ok) throw new Error(await res.text());
        checkoutSession = await res.json();

        result.innerText = 'Checkout session created: ' + checkoutSession.uuidData;

        // Now load addresses and cart
        await getAndShowCart();
        await loadAddresses();
    } catch (err) {
        result.innerText = 'Error: ' + err.message;
    }
});*/

document.addEventListener('DOMContentLoaded', async () => {
    try {
        const res = await fetch('/api/checkout/create', {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });
        if (!res.ok) throw new Error(await res.text());
        checkoutSession = await res.json();

        result.innerText = 'Checkout session created: ' + checkoutSession.uuidData;

        // Now load addresses and cart
        await getAndShowCart();
        await loadAddresses();
    } catch (err) {
        result.innerText = 'Error: ' + err.message;
    }
});

// Cart

async function getAndShowCart() {
    try {
        const response = await fetch('/api/cart/cartwithtotal');
        console.log(response);
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
        itemDiv.className = 'field';
        itemDiv.style.alignItems = 'center';
        itemDiv.style.gap = '12px';

        // Product name
        const nameLink = document.createElement('a');
        nameLink.href = `/product/${item.productId}`;
        nameLink.innerText = item.productName;
        nameLink.target = '_blank';
        nameLink.style.fontWeight = 'bold';
        nameLink.style.color = 'var(--color-primary)';

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

// Load user addresses
async function loadAddresses() {
    try {
        const res = await fetch('/api/shipment-address/all', {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });
        if (!res.ok) {

        };
        addresses = await res.json();

        const select = document.getElementById('addressSelect');
        select.innerHTML = ''; // Clear old options
        addresses.forEach(addr => {
            const option = document.createElement('option');
            option.value = addr.id;
            option.textContent = `${addr.fullName}, ${addr.street}, ${addr.city}, ${addr.country}`;
            select.appendChild(option);
        });

        document.getElementById('addressSection').style.display = 'flex';
    } catch (err) {
        result.innerText = 'Error: ' + err.message;
    }
}

// Place order (/api/checkout/close)
document.getElementById('placeOrderBtn').addEventListener('click', async () => {
    if (!checkoutSession) {
        result.innerText = 'No active checkout session.';
        return;
    }
    const paymentSelect = document.getElementById('paymentSelect');
    const selectedPaymentId = paymentSelect ? paymentSelect.value : null;
    if (!selectedPaymentId) {
        result.innerText = 'Please select a payment method.';
        return;
    }
    const selectedAddressId = document.getElementById('addressSelect').value;
    const checkoutData = {
        uuidData: checkoutSession.uuidData, // from session
        shipmentAddressId: parseInt(selectedAddressId),
        paymentMethodId: parseInt(selectedPaymentId), // not used for now
        cartFlag: checkoutSession.cartFlag
    };

    try {
        const res = await fetch('/api/checkout/close', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(checkoutData)
        });
        const text = await res.text();

        if (res.status === 405) {
            result.innerText = 'Cart was modified, please review your order.';
            const res2 = await fetch('/api/checkout/create', {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' }
            });
            if (!res2.ok) throw new Error(await res2.text());
            checkoutSession = await res2.json();
            await getAndShowCart();
            return;
        }

        if (!res.ok) throw new Error(text);

        if (res.status === 201) {
            result.innerText = 'Order created successfully, your order number is: ' + text
            + 'You will be redirected to order details and payment page in 3 seconds.';
            setTimeout(() => {
                window.location.href = `/order/${checkoutData.uuidData}`;
            }, 3000);
        }

        //result.innerText = text;
    } catch (err) {
        result.innerText = 'Error: ' + err.message;
    }
});