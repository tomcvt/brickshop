document.getElementById('shipmentForm').addEventListener('submit', async function (e) {
    e.preventDefault(); // prevent default form submission

    const form = e.target;

    const data = {
        fullName: form.fullName.value,
        street: form.street.value,
        zipCode: form.zipCode.value,
        city: form.city.value,
        country: form.country.value,
        phoneNumber: form.phoneNumber.value
        // userId can be added if your backend requires it
    };

    try {
        const response = await fetch('api/shipment-address/add', { // replace with your API endpoint
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        const messageEl = document.getElementById('message');
        if (response.ok) {
            //messageEl.textContent = 'Shipment address added successfully!';
            const savedAddress = await response.json();

            // Build a display string from the returned entity
            messageEl.innerHTML = `
                <strong>Shipment address added successfully!</strong><br>
                ID: ${savedAddress.id}<br>
                Full Name: ${savedAddress.fullName}<br>
                Street: ${savedAddress.street}<br>
                Zip Code: ${savedAddress.zipCode}<br>
                City: ${savedAddress.city}<br>
                Country: ${savedAddress.country}<br>
                Phone: ${savedAddress.phoneNumber}<br>
                User: ${savedAddress.user}
            `;
            messageEl.className = 'message success';
            messageEl.style.display = 'block';
            form.reset();
        } else {
            const errorData = await response.json();
            messageEl.textContent = errorData.message || 'Error adding shipment address.';
            messageEl.className = 'message error';
            messageEl.style.display = 'block';
        }
    } catch (error) {
        const messageEl = document.getElementById('message');
        messageEl.textContent = 'Network error: ' + error.message;
        messageEl.className = 'message error';
        messageEl.style.display = 'block';
    }
});