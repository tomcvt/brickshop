async function loadAndShowShipmentsToPack() {
    const messageDiv = document.getElementById('messageShipments');
    try {
        const response = await fetch('/api/packer/to-pack');
        if (!response.ok) {
            throw new Error('Failed to fetch shipments' + await response.text());
        }
        const shipments = await response.json();
        renderShipmentsToPack(shipments);
    } catch (error) {
        console.error('Error loading shipments:', error);
        messageDiv.innerText = 'Error fetching shipments: ' + error.message;
        messageDiv.className = 'alert alert-error';
    }
}

function renderShipmentsToPack(shipments) {
    const shipmentListDiv = document.getElementById('shipmentList');
    console.log('Shipments to pack:', shipments);
    shipmentListDiv.innerHTML = '';
    if (!shipments || shipments.length === 0) {
        shipmentListDiv.innerHTML = '<div class="alert alert-error">No shipments to pack.</div>';
        return;
    }
    shipments.forEach(shipment => {
        const div = document.createElement('div');
        div.className = 'field clr-primary';
        div.style.cursor = 'pointer';
        div.tabIndex = 0;
        div.setAttribute('role', 'button');
        div.setAttribute('aria-label', `Go to shipment order ${shipment.orderId}`);
        const packedBy = shipment.packedByUsername ? shipment.packedByUsername : '-';
        div.innerHTML = `
            <strong>Order #${shipment.orderId}</strong>
            <span>Packed By: ${packedBy}</span>
            <span>Status: ${shipment.status}</span>
        `;
        div.addEventListener('click', () => {
            window.location.href = `/packer/${shipment.orderId}`;
        });
        div.addEventListener('keypress', (e) => {
            if (e.key === 'Enter' || e.key === ' ') {
                window.location.href = `/packer/${shipment.orderId}`;
            }
        });
        shipmentListDiv.appendChild(div);
    });
}

document.addEventListener('DOMContentLoaded', () => {
    loadAndShowShipmentsToPack();
});