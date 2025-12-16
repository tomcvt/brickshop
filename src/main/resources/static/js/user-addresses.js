async function fetchShipmentAddresses() {
    try {
        const response = await fetch(`${window.location.origin}/api/user/address/all`); // replace with your API endpoint
        if (!response.ok) {
            const error = await response.json();
            const text = error.error + error.message;
            throw new Error(text);
        }
        const addresses = await response.json();
        console.log('Fetched addresses:', addresses);
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
    addressListEl.innerHTML = '';

    addresses.forEach(address => {
        const panel = document.createElement('div');
        panel.className = 'panelOption';
        panel.classList.add('wide');
        panel.style.marginBottom = '16px';
        panel.style.position = 'relative';

        // Address info
        const info = document.createElement('div');
        info.innerHTML = `
            <div class="name"><strong>${address.fullName}</strong></div>
            <div class="street">${address.street}, ${address.zipCode}</div>
            <div class="city-country">${address.city}, ${address.country}</div>
            <div class="phone">📞 ${address.phoneNumber}</div>
        `;
        panel.appendChild(info);

        // Edit button
        const editBtn = document.createElement('button');
        editBtn.className = 'btn btn-small';
        editBtn.title = 'Edit address';
        editBtn.style.position = 'absolute';
        editBtn.style.top = '10px';
        editBtn.style.right = '40px';
        editBtn.innerHTML = '✏️'; // Pencil emoji
        editBtn.addEventListener('click', () => openEditModal(address, true));

        // Delete button
        const deleteBtn = document.createElement('button');
        deleteBtn.className = 'btn btn-small';
        deleteBtn.title = 'Delete address';
        deleteBtn.style.position = 'absolute';
        deleteBtn.style.top = '10px';
        deleteBtn.style.right = '10px';
        deleteBtn.innerHTML = '🛑'; // Crossed circle emoji
        deleteBtn.addEventListener('click', () => confirmDeleteAddress(address.publicId));

        panel.appendChild(editBtn);
        panel.appendChild(deleteBtn);
        addressListEl.appendChild(panel);
    });
}

// Modal logic
const editModal = document.getElementById('editAddressModal');
const closeEditModalBtn = document.getElementById('closeEditModal');
const editAddressForm = document.getElementById('editAddressForm');
const editModalMessage = document.getElementById('editModalMessage');
const addAddressBtn = document.getElementById('addAddressBtn');
let isEditMode = false;

function openEditModal(address = {}, editMode = false) {
    isEditMode = editMode;
    document.getElementById('editAddressPublicId').value = address.publicId || '';
    document.getElementById('editFullName').value = address.fullName || '';
    document.getElementById('editStreet').value = address.street || '';
    document.getElementById('editZipCode').value = address.zipCode || '';
    document.getElementById('editCity').value = address.city || '';
    document.getElementById('editCountry').value = address.country || '';
    document.getElementById('editPhoneNumber').value = address.phoneNumber || '';
    editModalMessage.style.display = 'none';
    editModal.style.display = 'flex';
}

addAddressBtn.addEventListener('click', () => openEditModal({}, false));

closeEditModalBtn.addEventListener('click', () => {
    editModal.style.display = 'none';
});

window.addEventListener('click', (e) => {
    if (e.target === editModal) {
        editModal.style.display = 'none';
    }
});

editAddressForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const publicId = document.getElementById('editAddressPublicId').value;
    const addressData = {
        publicId: publicId,
        fullName: document.getElementById('editFullName').value,
        street: document.getElementById('editStreet').value,
        zipCode: document.getElementById('editZipCode').value,
        city: document.getElementById('editCity').value,
        country: document.getElementById('editCountry').value,
        phoneNumber: document.getElementById('editPhoneNumber').value
    };
    try {
        let url, successMsg;
        if (isEditMode) {
            url = `${window.location.origin}/api/user/address/update`;
            successMsg = 'Address updated successfully!';
        } else {
            url = `${window.location.origin}/api/user/address/add`;
            successMsg = 'Address added successfully!';
        }
        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(addressData)
        });
        if (response.ok) {
            editModalMessage.textContent = successMsg;
            editModalMessage.className = 'alert alert-success';
            editModalMessage.style.display = 'block';
            setTimeout(() => {
                editModal.style.display = 'none';
                fetchShipmentAddresses();
            }, 1000);
        } else {
            const error = await response.json();
            const errText = error.error + ":" + error.message;
            editModalMessage.textContent = 'Error: ' + errText;
            editModalMessage.className = 'alert alert-error';
            editModalMessage.style.display = 'block';
        }
    } catch (err) {
        editModalMessage.textContent = 'Network error: ' + err.message;
        editModalMessage.className = 'alert alert-error';
        editModalMessage.style.display = 'block';
    }
});

// Delete logic with confirmation
async function confirmDeleteAddress(publicId) {
    if (!confirm('Are you sure you want to delete this address?')) return;
    try {
        const response = await fetch(`${window.location.origin}/api/user/address/delete`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ publicId })
        });
        if (response.ok) {
            fetchShipmentAddresses();
        } else {
            const error = await response.json();
            alert('Failed to delete address: ' + error.error + ":" + error.message);
        }
    } catch (err) {
        alert('Network error: ' + err.message);
    }
}

// Fetch and render addresses on page load
fetchShipmentAddresses();