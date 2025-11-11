
/*

document.addEventListener("DOMContentLoaded", async () => {
    const productDataDiv = document.getElementById("product-data");
    const productId = productDataDiv.dataset.productId;

    // Fetch product data
    const response = await fetch(`/api/admin/images/ordering-request/${productId}`);
    const product = await response.json();

    // ---- Part 1: Render product details ----
    const detailsDiv = document.getElementById("product-details");
    detailsDiv.innerHTML = `
                <h2>${product.name}</h2>
                <p>${product.description}</p>
                <p><strong>Price:</strong> $${product.price}</p>
                <p><strong>Stock:</strong> ${product.stock}</p>
            `;

    // ---- Part 2: Render images ----
    const gallery = document.getElementById("image-gallery");
    product.imageUrls.forEach(url => {
        const img = document.createElement("img");
        img.src = "/outsideimages/" + url;
        img.dataset.uri = url; // keep the uuid or URI
        img.classList.add("draggable-image");
        img.draggable = true;
        gallery.appendChild(img);
    });

    enableDragAndDrop(gallery);

    // Save button
    document.getElementById("save-order").addEventListener("click", () => {
        const newOrder = [...gallery.querySelectorAll("img")].map(img => img.dataset.uri);
        const productImageDto = {productId: productId, imageUrls: newOrder};
        console.log("New Image Order:", newOrder);
        console.log("ProductImageDto:", productImageDto);
        // Example fetch to save order
        fetch(`/api/admin/images/post-ordering`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(productImageDto)
        }).then(r => {
            if (r.ok) {
                alert("Order saved!");
            } else {
                alert("Failed to save order.");
            }
        });
    });
});

function enableDragAndDrop(container) {
    let draggedEl = null;

    container.addEventListener("dragstart", (e) => {
        if (e.target.tagName === "IMG") {
            draggedEl = e.target;
            e.target.classList.add("dragging");
        }
    });

    container.addEventListener("dragend", (e) => {
        if (e.target.tagName === "IMG") {
            e.target.classList.remove("dragging");
        }
        draggedEl = null;
    });

    container.addEventListener("dragover", (e) => {
        e.preventDefault();
        const afterElement = getDragAfterElement(container, e.clientX);
        if (afterElement == null) {
            container.appendChild(draggedEl);
        } else {
            container.insertBefore(draggedEl, afterElement);
        }
    });
}

function getDragAfterElement(container, x) {
    const draggableElements = [...container.querySelectorAll("img:not(.dragging)")];
    return draggableElements.reduce((closest, child) => {
        const box = child.getBoundingClientRect();
        const offset = x - box.left - box.width / 2;
        if (offset < 0 && offset > closest.offset) {
            return { offset: offset, element: child };
        } else {
            return closest;
        }
    }, { offset: Number.NEGATIVE_INFINITY }).element;
}


*/