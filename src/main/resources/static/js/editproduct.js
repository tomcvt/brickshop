let product = null;
let publicId = null;

document.addEventListener('DOMContentLoaded', async () => {
    const productDataDiv = document.getElementById('product-data');
    publicId = productDataDiv.dataset.publicId;
    console.log('Editing Product Public ID:', publicId);

    // Fetch product data and categories
    const response = await fetch(`/api/admin/edit-product/${publicId}`);
    product = await response.json();
    const categoriesResponse = await fetch('/api/products/categories');
    const categories = await categoriesResponse.json();
    console.log('Fetched Categories:', categories);
    console.log('Fetched Product:', product);

    // Get static elements
    const detailsDiv = document.getElementById('product-details');
    const editBox = document.getElementById('product-edit-box');
    const editBtn = document.getElementById('edit-product-btn');
    const newCategoryInput = document.getElementById('new-category');
    const categoriesListDiv = document.getElementById('categories-list');
    const saveOrderBtn = document.getElementById('save-order');
    const productCategories = product.categoriesNames; // categories that should be pre-checked
    renderCategories(categories, productCategories, categoriesListDiv);

    // Render initial view mode
    setProductView(product);
    setProductEdit(product);
    detailsDiv.style.display = '';
    editBox.style.display = 'none';
    saveOrderBtn.disabled = true;

    let inEditMode = false;
    editBtn.addEventListener('click', () => {
        inEditMode = !inEditMode;
        if (inEditMode) {
            setProductEdit(product);
            detailsDiv.style.display = 'none';
            editBox.style.display = '';
            editBtn.textContent = 'View';
            saveOrderBtn.disabled = false;
        } else {
            setProductView(product);
            detailsDiv.style.display = '';
            editBox.style.display = 'none';
            editBtn.textContent = 'Edit';
            saveOrderBtn.disabled = true;
        }
    });

    // ---- Part 2: Render images ----
    const gallery = document.getElementById('image-gallery');
    renderGallery(product.imageUrls, gallery);
    enableDragAndDrop(gallery);

    // Save Order button logic
    saveOrderBtn.addEventListener('click', async () => {
        const newOrder = [...gallery.querySelectorAll('img')].map(img => img.dataset.uri);
        const checkedCategories = [];
        const checkboxes = categoriesListDiv.querySelectorAll('input[type="checkbox"]');
        checkboxes.forEach(cb => {
            if (cb.checked) checkedCategories.push(cb.value);
        });
        let name = product.name;
        let description = product.description;
        let price = product.price;
        let stock = product.stock;
        let htmlDescription = product.htmlDescription;
        if (inEditMode) {
            name = document.getElementById('edit-name').value;
            description = document.getElementById('edit-description').value;
            price = document.getElementById('edit-price').value;
            stock = document.getElementById('edit-stock').value;
            htmlDescription = document.getElementById('edit-html-description').value;
        }
        const ProductDto = {
            publicId: publicId,
            name,
            description,
            price,
            stock,
            imageUrls: newOrder,
            categoriesNames: checkedCategories,
            htmlDescription: htmlDescription
        };
        console.log('New Image Order:', newOrder);
        console.log('ProductImageDto:', ProductDto);
        const r = await fetch(`/api/admin/edit-product`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(ProductDto)
        });
        if (r.ok) {
            alert('Product info saved!');
            // Update product object and switch back to view mode
            product.name = name;
            product.description = description;
            product.price = price;
            product.stock = stock;
            product.imageUrls = newOrder;

            window.location.reload();
            
            setProductView(product);
            setProductEdit(product);
            detailsDiv.style.display = '';
            editBox.style.display = 'none';
            editBtn.textContent = 'Edit';
            inEditMode = false;
            saveOrderBtn.disabled = true;
        } else {
            const errorResponse = await r.json();
            alert('Error saving product: ' + errorResponse.message);
        }
    });
    // Image upload logic
    document.getElementById('image-upload-form').addEventListener('submit', async (event) => {
        event.preventDefault();
        const file = document.getElementById('image-file');
        if (file.files.length === 0) {
            alert('Please select a file to upload.');
            return;
        }
        const imageFile = file.files[0];
        const newImageOrder = gallery.querySelectorAll('img').length + 1;
        // Upload the image by publicID
        const uploadResponse = await uploadImage(imageFile, publicId, newImageOrder);
        if (uploadResponse.ok) {
            const result = await uploadResponse.text();
            product.imageUrls.push(result);
            renderGallery(product.imageUrls, gallery);
            enableDragAndDrop(gallery);
            file.value = ''; // Clear the file input
            alert('Image uploaded successfully!');
        } else {
            const errorResponse = await uploadResponse.json();
            alert('Error uploading image: ' + errorResponse.message);
        }
    });

    document.getElementById('add-category-btn').addEventListener('click', async () => {
        const categoryName = newCategoryInput.value.trim();
        if (categoryName === '') {
            alert('Category name cannot be empty.');
            return;
        }
        try {
            const response = await fetch('/api/admin/add-category', {
                method: 'POST',
                headers: { 'Content-Type': 'text/plain' },
                body: categoryName
            });
            if (response.ok) {
                //const newCategory = await response.text();
                addCategory(categoriesListDiv, categoryName);
                newCategoryInput.value = ''; // Clear input field
                alert(`Category "${newCategory}" added successfully!`);
            } else {
                const error = await response.json();
                alert(`Failed to add category: ${error.message}`);
            }
        } catch (error) {
            console.error('Error adding category:', error);
            alert('An error occurred while adding the category.');
        }
    });
});

function addCategory(categoriesListDiv, categoryName) {
    const label = document.createElement('label');
    label.style.display = 'block';
    const checkbox = document.createElement('input');
    checkbox.type = 'checkbox';
    checkbox.value = categoryName;
    label.appendChild(checkbox);
    label.appendChild(document.createTextNode(categoryName));
    const deletebutton = document.createElement('button');
    deletebutton.textContent = '-';
    deletebutton.classList.add('delete-category-btn');
    deletebutton.addEventListener('click', async () => {
        try {
            const response = await fetch('/api/admin/delete-category', {
                method: 'DELETE',
                headers: { 'Content-Type': 'text/plain' },
                body: categoryName
            });
            if (response.ok) {
                categoriesListDiv.removeChild(label);
                alert(`Category "${categoryName}" deleted successfully!`);
            } else {
                const error = await response.json();
                alert(`Failed to delete category: ${error.message}`);
            }
        } catch (error) {
            console.error('Error deleting category:', error);
            alert('An error occurred while deleting the category.');
        }
    });
    label.appendChild(deletebutton);
    categoriesListDiv.appendChild(label);
}

// Function to render the checkboxes dynamically
function renderCategories(categories, productCategories, categoriesListDiv) {
    categoriesListDiv.innerHTML = ''; // Clear previous content if any
    categories.forEach(category => {
        const label = document.createElement('label');
        label.style.display = 'block';

        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.value = category;
        if (productCategories.includes(category)) {
            checkbox.checked = true; // pre-check if in productCategories
        }
        
        label.appendChild(checkbox);
        label.appendChild(document.createTextNode(category));
        const deletebutton = document.createElement('button');
        deletebutton.classList.add('delete-category-btn');
        deletebutton.textContent = '-';
        deletebutton.addEventListener('click', async () => {
            try {
                const response = await fetch('/api/admin/delete-category', {
                    method: 'DELETE',
                    headers: { 'Content-Type': 'text/plain' },
                    body: category
                });
                if (response.ok) {
                    categoriesListDiv.removeChild(label);
                    alert(`Category "${category}" deleted successfully!`);
                } else {
                    const error = await response.json();
                    alert(`Failed to delete category: ${error.message}`);
                }
            } catch (error) {
                console.error('Error deleting category:', error);
                alert('An error occurred while deleting the category.');
            }
        });
        label.appendChild(deletebutton);
        categoriesListDiv.appendChild(label);
    });
}

function setProductView(product) {
    document.getElementById('product-name').textContent = product.name;
    document.getElementById('product-description').textContent = product.description;
    document.getElementById('product-price').textContent = `${product.price} zł`;
    document.getElementById('product-stock').textContent = product.stock;
    const htmlDescriptionDiv = document.getElementById('product-html-description');
    console.log(htmlDescriptionDiv);
    document.getElementById('product-html-description').innerHTML = product.htmlDescription || '';
}

function setProductEdit(product) {
    document.getElementById('edit-name').value = product.name;
    document.getElementById('edit-description').value = product.description;
    document.getElementById('edit-price').value = product.price;
    document.getElementById('edit-stock').value = product.stock;
    document.getElementById('edit-html-description').value = product.htmlDescription || '';
}

function renderGallery(imageUrls, gallery) {
    gallery.innerHTML = '';
    imageUrls.forEach(url => {
        const img = document.createElement('img');
        img.src = '/outsideimages/' + url;
        img.dataset.uri = url;
        img.classList.add('draggable-image', 'card', 'gallery-image');
        img.draggable = true;
        gallery.appendChild(img);
    });
}

function enableDragAndDrop(container) {
    let draggedEl = null;

    container.addEventListener('dragstart', (e) => {
        if (e.target.tagName === 'IMG') {
            draggedEl = e.target;
            e.target.classList.add('dragging');
        }
    });

    container.addEventListener('dragend', (e) => {
        if (e.target.tagName === 'IMG') {
            e.target.classList.remove('dragging');
        }
        draggedEl = null;
    });

    container.addEventListener('dragover', (e) => {
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
    const draggableElements = [...container.querySelectorAll('img:not(.dragging)')];
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

function uploadImage(file, publicId, imageOrder) {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('publicId', publicId);
    formData.append('imageOrder', imageOrder);
    return fetch('/api/upload/product', {
        method: 'POST',
        body: formData
    });
}
