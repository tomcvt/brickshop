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
    shipmentListDiv.innerHTML = JSON.stringify(shipments, null, 2); // Simple rendering for now
}

document.addEventListener('DOMContentLoaded', () => {
    loadAndShowShipmentsToPack();
});