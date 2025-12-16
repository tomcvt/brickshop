import { initSearchModule } from './pageModule.js';

// Configurable variables
let currentPage = 0;
let pageSize = 10;
let currentStatus = 'PENDING';
const endpoint = '/api/packer/search';

function renderShipmentsToPack(simplePage) {
    const shipmentListDiv = document.getElementById('shipmentList');
    shipmentListDiv.innerHTML = '';
    if (!simplePage || !simplePage.content || simplePage.content.length === 0) {
        shipmentListDiv.innerHTML = '<div class="alert alert-error">No shipments in this category.</div>';
        return;
    }
    simplePage.content.forEach(shipment => {
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
    // Status button logic
    const statusBtns = document.querySelectorAll('.status-btn');
    let lastSelectedBtn = null;
    statusBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            // Unselect previous
            if (lastSelectedBtn) lastSelectedBtn.classList.remove('btn-primary');
            // Select current
            btn.classList.add('btn-primary');
            lastSelectedBtn = btn;
            // Set currentStatus and trigger hidden search button
            currentStatus = btn.getAttribute('data-status');
            document.getElementById('searchButtonStatus').click();
        });
    });
    // Set default selected
    if (statusBtns.length > 0) {
        statusBtns[0].classList.add('btn-primary');
        lastSelectedBtn = statusBtns[0];
    }

    initSearchModule(
        (results, opts) => {
            currentPage = opts.page;
            pageSize = opts.size;
            currentStatus = opts.state;
            console.log(`Rendering page ${currentPage} with size ${pageSize} and status ${currentStatus}`);
            renderShipmentsToPack(results);
        },
        {
            navBarIds: ['navPanelShipments'],
            getCurrentPage: () => currentPage,
            getSize: () => pageSize,
            getState: () => currentStatus,
            getEndpoint: () => endpoint,
            searchButtonId: 'searchButtonStatus'
        }
    );

    document.getElementById('searchButtonStatus').click();
});