
/**
 * Mock PayPal-like payment module for dev testing
 * 
 * Usage:
 *   MockPayPal.Buttons({
 *     createOrder: () => 'ORDER123',
 *     onApprove: (data) => { console.log('Paid:', data); }
 *   }).render('#paypal-button-container');
 */

const MockPayPal = (() => {
    const payments = {};

    return {
        Buttons: function (config) {
            return {
                render: function (selector) {
                    const container = document.querySelector(selector);
                    if (!container) {
                        console.error('MockPayPal: container not found:', selector);
                        return;
                    }

                    const btn = document.createElement('button');
                    btn.textContent = 'Mock Pay Now';
                    btn.style.padding = '10px 20px';
                    btn.style.margin = '10px 0';
                    btn.style.border = 'none';
                    btn.style.borderRadius = '5px';
                    btn.style.background = '#0070ba';
                    btn.style.color = '#fff';
                    btn.style.cursor = 'pointer';

                    btn.addEventListener('click', async () => {
                        // Step 1: Create the order (instant in real life)
                        const orderId =
                            (typeof config.createOrder === 'function'
                                ? config.createOrder()
                                : 'ORDER-' + Date.now());

                        // Step 2: Simulate user approval + backend capture (with loading)
                        btn.disabled = true;
                        btn.textContent = 'Processing...';

                        await new Promise((resolve) => setTimeout(resolve, 3000));

                        // Mark as paid
                        payments[orderId] = true;

                        // Step 3: Call onApprove callback
                        if (typeof config.onApprove === 'function') {
                            config.onApprove({ orderID: orderId, paid: true });
                        }

                        btn.textContent = 'Paid ✓';
                    });

                    container.innerHTML = '';
                    container.appendChild(btn);
                },
            };
        },

        isPaid(orderId) {
            return !!payments[orderId];
        },
    };
})();

if (typeof window !== 'undefined') {
    window.MockPayPal = MockPayPal;
}
export default MockPayPal;
