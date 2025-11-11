import * as SidebarCart from './sidebar-cart.js';

SidebarCart.loadAndShowCart();

const toggleHeader = document.getElementById('togglePasswordForm');
const form = document.getElementById('passwordForm');
const panel = document.getElementById('passwordPanel');
let panelOpen = false;

// Toggle function
toggleHeader.addEventListener('click', (event) => {
    event.stopPropagation();
    panelOpen = !panelOpen;
    if (panelOpen) {
        form.style.maxHeight = '500px';
        panel.classList.add('open');
    } else {
        form.style.maxHeight = '0';
        panel.classList.remove('open');
    }
});

// Close if clicking outside
document.addEventListener('click', (event) => {
    if (panelOpen && !form.contains(event.target) && event.target !== toggleHeader) {
        form.style.maxHeight = '0';
        panel.classList.remove('open');
        panelOpen = false;
    }
});

// Handle password form submission
document.getElementById('passwordForm').addEventListener('submit', async (event) => {
    event.preventDefault();

    const oldPassword = document.getElementById('oldPassword').value;
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const messageDiv = document.getElementById('messagePassword');

    if (newPassword !== confirmPassword) {
        messageDiv.innerText = 'New passwords do not match!';
        messageDiv.className = 'alert alert-error';
        messageDiv.style.display = 'block';
        return;
    }

    const params = {
        oldPassword: oldPassword,
        newPassword: newPassword
    }

    try {
        const response = await fetch('/api/user/change-password', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(params)
        });

        if (response.ok) {
            messageDiv.innerText = 'Password updated successfully!';
            messageDiv.className = 'alert alert-success';
            messageDiv.style.display = 'block';
            form.reset();
            form.style.maxHeight = '0';
            panel.classList.remove('open');
            panelOpen = false;
        } else {
            const error = await response.text();
            messageDiv.innerText = 'Error: ' + error;
            messageDiv.className = 'alert alert-error';
            messageDiv.style.display = 'block';
        }
    } catch (err) {
        messageDiv.innerText = 'Network error: ' + err.message;
        messageDiv.className = 'alert alert-error';
        messageDiv.style.display = 'block';
    }
});

/* ORDERS */

const toggleOrdersHeader = document.getElementById('toggleOrdersPanel');
const ordersForm = document.getElementById('ordersForm');
const ordersPanel = document.getElementById('ordersPanel');
const ordersArrow = document.getElementById('ordersArrow');
const messageOrders = document.getElementById('messageOrders');
let ordersOpen = false;

// Toggle function
toggleOrdersHeader.addEventListener('click', async (event) => {
    event.stopPropagation();
    ordersOpen = !ordersOpen;

    if (ordersOpen) {
        ordersPanel.classList.add('open');
        ordersForm.style.maxHeight = '500px';
        ordersArrow.style.transform = 'rotate(180deg)';

        // Fetch orders only if the container is empty
        if (!ordersForm.hasChildNodes()) {
            try {
                const response = await fetch('/api/user/orders/summaries');
                if (!response.ok) throw new Error(await response.text());
                const orders = await response.json();

                if (orders.length === 0) {
                    ordersForm.innerHTML = '<p>No orders found.</p>';
                } else {
                    const list = document.createElement('ul');
                    list.style.listStyle = 'none';
                    list.style.padding = '0';
                    orders.forEach(order => {
                        const li = document.createElement('li');
                        li.className = 'orderRow';
                        li.style.padding = '0.5rem';
                        li.style.cursor = 'pointer';
                        li.style.borderBottom = '1px solid #ddd';

                        li.innerHTML = `
                            <strong>Order #${order.orderId}</strong> —
                            Status: ${order.status}, Payment: ${order.paymentStatus},
                            Method: ${order.paymentMethod}, Total: ${order.totalAmount} zł
                        `;

                        li.addEventListener('click', () => {
                            window.location.href = `/user/orders/${order.orderId}`;
                        });

                        list.appendChild(li);
                    });
                    ordersForm.appendChild(list);
                }
            } catch (err) {
                messageOrders.innerText = 'Error loading orders: ' + err.message;
                messageOrders.className = 'alert alert-error';
                messageOrders.style.display = 'block';
            }
        }

    } else {
        ordersPanel.classList.remove('open');
        ordersForm.style.maxHeight = '0';
    }
});

// Close if clicking outside
document.addEventListener('click', (event) => {
    if (ordersOpen && !ordersForm.contains(event.target) && event.target !== toggleOrdersHeader) {
        ordersForm.style.maxHeight = '0';
        ordersPanel.classList.remove('open');
        ordersOpen = false;
    }
});


