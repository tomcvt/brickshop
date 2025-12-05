document.addEventListener('DOMContentLoaded', () => {
    const orderId = document.body.getAttribute('data-order-id');
    if (!orderId) {
        document.getElementById('order-details-panel').innerHTML = '<div class="alert alert-error">Order ID not found.</div>';
        return;
    }
    fetch(`/api/admin/orders/${orderId}`)
        .then(res => res.json())
        .then(order => renderOrderDetails(order))
        .catch(() => {
            document.getElementById('order-details-panel').innerHTML = '<div class="alert alert-error">Failed to load order details.</div>';
        });
});

function renderOrderDetails(order) {
    const panel = document.getElementById('order-details-panel');
    panel.innerHTML = '';
    // Title
    const title = document.createElement('div');
    title.className = 'order-details-title';
    title.textContent = `Order #${order.orderId}`;
    panel.appendChild(title);

    // Helper to add a field row
    function addField(label, value, highlight = false) {
        const field = document.createElement('div');
        field.className = 'field';
        const row = document.createElement('div');
        row.className = 'field-row';
        const labelDiv = document.createElement('div');
        labelDiv.className = 'field-label';
        labelDiv.textContent = label;
        const valueDiv = document.createElement('div');
        valueDiv.className = 'cell' + (highlight ? ' highlight' : '');
        valueDiv.textContent = value ?? '';
        row.appendChild(labelDiv);
        row.appendChild(valueDiv);
        field.appendChild(row);
        panel.appendChild(field);
    }

    addField('Order ID', order.orderId);
    addField('Username', order.username);
    addField('Email', order.email);
    addField('Cart ID', order.cartId);
    addField('Shipping Address', order.shippingAddressString);
    addField('Order Status', order.status, true);
    addField('Created At', order.createdAt);
    addField('Total Amount', order.totalAmount);
    addField('Payment Method', order.paymentMethod);
    addField('Checkout Session ID', order.checkoutSessionId);

    // Current Transaction
    if (order.currentTransaction) {
        const field = document.createElement('div');
        field.className = 'field';
        const row = document.createElement('div');
        row.className = 'field-row';
        const labelDiv = document.createElement('div');
        labelDiv.className = 'field-label';
        labelDiv.textContent = 'Current Transaction';
        row.appendChild(labelDiv);
        const txDiv = document.createElement('div');
        txDiv.className = 'cell highlight';
        txDiv.innerHTML = renderTransaction(order.currentTransaction);
        row.appendChild(txDiv);
        field.appendChild(row);
        panel.appendChild(field);
    }

    // Transactions (expandable)
    if (order.transactions && order.transactions.length > 0) {
        const field = document.createElement('div');
        field.className = 'field';
        const row = document.createElement('div');
        row.className = 'field-row';
        const labelDiv = document.createElement('div');
        labelDiv.className = 'field-label transactions-toggle';
        labelDiv.textContent = 'Transactions';
        labelDiv.tabIndex = 0;
        labelDiv.setAttribute('role', 'button');
        labelDiv.setAttribute('aria-expanded', 'false');
        row.appendChild(labelDiv);
        const txList = document.createElement('div');
        txList.className = 'transactions-list';
        order.transactions.forEach(tx => {
            const txRow = document.createElement('div');
            txRow.className = 'transaction-row';
            txRow.innerHTML = renderTransaction(tx);
            txList.appendChild(txRow);
        });
        row.appendChild(txList);
        field.appendChild(row);
        panel.appendChild(field);
        // Toggle logic
        labelDiv.addEventListener('click', () => {
            const open = txList.classList.toggle('open');
            labelDiv.setAttribute('aria-expanded', open ? 'true' : 'false');
        });
        labelDiv.addEventListener('keypress', e => {
            if (e.key === 'Enter' || e.key === ' ') {
                labelDiv.click();
            }
        });
    }

    // Cart section placeholder
    const cartSection = document.createElement('div');
    cartSection.id = 'cart-section-placeholder';
    cartSection.textContent = 'Cart details will be displayed here.';
    panel.appendChild(cartSection);
}

function renderTransaction(tx) {
    return `
        <span class="transaction-label">ID:</span> <span>${tx.transactionId ?? ''}</span>
        <span class="transaction-label">Status:</span> <span>${tx.status ?? ''}</span>
        <span class="transaction-label">Method:</span> <span>${tx.paymentMethod ?? ''}</span>
        <span class="transaction-label">Created:</span> <span>${tx.createdAt ? tx.createdAt.replace('T', ' ').slice(0, 19) : ''}</span>
        <span class="transaction-label">Updated:</span> <span>${tx.updatedAt ? tx.updatedAt.replace('T', ' ').slice(0, 19) : ''}</span>
        <span class="transaction-label">Amount:</span> <span>${tx.amount ?? ''}</span>
        <span class="transaction-label">Token:</span> <span>${tx.paymentToken ?? ''}</span>
    `;
}
