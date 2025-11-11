import MockPayPal from './mockpp.js ';

let ppinitialized = false;

let sessionId = null;
document.addEventListener('DOMContentLoaded', async () => {
    sessionId = document.getElementById('sessionId').getAttribute('data-session-id');
    if (!sessionId) {
        showError('Invalid order session.');
        return;
    }
    await loadOrder(sessionId);
});

async function loadOrder(sessionId) {
    try {
        const res = await fetch(`/api/orders/withsession/${sessionId}`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });

        if (res.status === 401 || res.status === 403 || res.status === 404) {
            throw new Error(await res.text());
        }

        if (!res.ok) throw new Error(await res.text());

        const order = await res.json();
        const orderId = order.orderId;
        if (!ppinitialized) {
            setupMockPayPalButton(orderId);
            ppinitialized = true;
        }
        //console.log(order);
        renderOrder(order);
    } catch (err) {
        showError('Failed to load order: ' + err.message);
        setTimeout(() => { window.location.href = '/user'; }, 3000);
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

// Mock PayPal, would use real integration here

function setupMockPayPalButton(orderId) {
    MockPayPal.Buttons({
        createOrder: () => `${orderId}`,
        onApprove: async (data) => {
            console.log('Payment approved for order:', data.orderID);
            const res = await fetch(`/api/orders/payment/verify?orderId=${data.orderID}`, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' }
            });
            if (!res.ok) {
                const text = await res.text();
                showError('Payment verification failed: ' + text);
                return;
            }
            setTimeout(async () => {await loadOrder(sessionId)}, 1000);
        }
    }).render('#paypal-button-container');
}