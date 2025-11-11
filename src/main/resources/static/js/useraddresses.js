async function fetchShipmentAddresses() {
    try {
        const response = await fetch('api/shipment-address/all'); // replace with your API endpoint
        if (!response.ok) {
            throw new Error('Failed to fetch addresses');
        }
        const addresses = await response.json();
        renderShipmentAddresses(addresses);
    } catch (error) {
        console.error('Error fetching shipment addresses:', error);
        const addressListEl = document.getElementById('addressList');
        addressListEl.innerHTML = `<div class="address-card" style="color:red;">Error loading addresses: ${error.message}</div>`;
    }
}

// Function to render addresses
function renderShipmentAddresses(addresses) {
    const addressListEl = document.getElementById('addressList');
    addressListEl.innerHTML = ''; // clear previous content

    addresses.forEach(address => {
        const card = document.createElement('div');
        card.className = 'address-card';

        card.innerHTML = `
                <div class="name">${address.fullName}</div>
                <div class="street">${address.street}, ${address.zipCode}</div>
                <div class="city-country">${address.city}, ${address.country}</div>
                <div class="phone">📞 ${address.phoneNumber}</div>
            `;

        addressListEl.appendChild(card);
    });
}

// Fetch and render addresses on page load
fetchShipmentAddresses();