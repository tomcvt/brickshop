import { initOrderSearchModule } from '/js/pageOrdersModule.js';

let currentPage = 0;
let pageSize = 10;
let username = '';
let orderStatus = '';
let paymentMethod = '';
let createdBefore = new Date().toISOString().slice(0, 19);
const endpoint = '/api/admin/orders/search';

function renderOrders(simplePage) {
    const listDiv = document.getElementById('ordersList');
    listDiv.innerHTML = '';
    if (!simplePage || !simplePage.content || simplePage.content.length === 0) {
        listDiv.innerHTML = '<div class="alert alert-error">No orders found.</div>';
        return;
    }
    simplePage.content.forEach(order => {
        const row = document.createElement('div');
        row.className = 'field order-row';
        row.style.display = 'flex';
        row.style.width = '100%';
        row.innerHTML = `
            <div class="order-cell order-id">${order.orderId ?? ''}</div>
            <div class="order-cell username">${order.username ?? ''}</div>
            <div class="order-cell cart-id">${order.cartId ?? ''}</div>
            <div class="order-cell status">${order.status ?? ''}</div>
            <div class="order-cell payment-method">${order.paymentMethod ?? ''}</div>
            <div class="order-cell created-at">${order.createdAt ? order.createdAt.replace('T', ' ').slice(0, 19) : ''}</div>
            <div class="order-cell total-amount">${order.totalAmount ?? ''}</div>
            <div class="order-cell details-btn-cell">
                <button class="btn btn-primary btn-small" onclick="window.location.href='/admin/manage-orders/${order.orderId}'">Details</button>
            </div>
        `;
        listDiv.appendChild(row);
    });
}

document.addEventListener('DOMContentLoaded', () => {
    // Header filter logic
    const usernameInput = document.getElementById('filter-username');
    const orderStatusSelect = document.getElementById('filter-orderStatus');
    const paymentMethodSelect = document.getElementById('filter-paymentMethod');
    const createdBeforeInput = document.getElementById('filter-createdBefore');
    const searchBtn = document.getElementById('searchBtn');

    usernameInput.addEventListener('input', e => { username = e.target.value; });
    orderStatusSelect.addEventListener('change', e => { orderStatus = e.target.value; });
    paymentMethodSelect.addEventListener('change', e => { paymentMethod = e.target.value; });
    createdBeforeInput.addEventListener('input', e => { createdBefore = e.target.value; });

    initOrderSearchModule(
        (results, opts) => {
            currentPage = opts.page;
            pageSize = opts.size;
            username = opts.username;
            orderStatus = opts.orderStatus;
            paymentMethod = opts.paymentMethod;
            createdBefore = opts.createdBefore;
            renderOrders(results);
        },
        {
            navBarIds: ['ordersNavBar'],
            getCurrentPage: () => currentPage,
            getUsername: () => username,
            getOrderStatus: () => orderStatus,
            getPaymentMethod: () => paymentMethod,
            getCreatedBefore: () => createdBefore,
            getSize: () => pageSize,
            getEndpoint: () => endpoint,
            searchButtonId: 'searchBtn'
        }
    );

    // Set default date to now
    createdBeforeInput.value = createdBefore;
});
