let categories = [];
let selectedCategories = [];
let images = [];
let imageOrder = [];

document.addEventListener('DOMContentLoaded', async () => {
	// Fetch categories from backend
	const categoriesResponse = await fetch('/api/products/categories');
	categories = await categoriesResponse.json();
	selectedCategories = [];
	renderCategories(categories, selectedCategories);

	// Category add
	document.getElementById('add-category-btn').addEventListener('click', async () => {
		const newCategoryInput = document.getElementById('new-category');
		const categoryName = newCategoryInput.value.trim();
		if (!categoryName) {
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
				// Expecting a success object or empty
				categories.push(categoryName);
				renderCategories(categories, selectedCategories);
				newCategoryInput.value = '';
				alert(`Category "${categoryName}" added successfully!`);
			} else {
				let errorMsg = 'Failed to add category.';
				try {
					const errObj = await response.json();
					if (errObj && errObj.message) {
						errorMsg = errObj.message;
					}
				} catch (e) {
					errorMsg = await response.text();
				}
				alert(`Failed to add category: ${errorMsg}`);
			}
		} catch (error) {
			console.error('Error adding category:', error);
			alert('An error occurred while adding the category.');
		}
	});

	// Image upload and preview
	const imageInput = document.getElementById('image-files');
	const gallery = document.getElementById('image-gallery');
	imageInput.addEventListener('change', (event) => {
		images = Array.from(event.target.files);
		imageOrder = images.map((_, idx) => idx); // initial order
		renderGallery(images, imageOrder, gallery);
		enableDragAndDrop(gallery);
	});

	// Form submit
	document.getElementById('add-product-form').addEventListener('submit', async (event) => {
		event.preventDefault();
		// Collect product info
		const name = document.getElementById('product-name').value.trim();
		const description = document.getElementById('product-description').value.trim();
		const price = document.getElementById('product-price').value;
		const stock = document.getElementById('product-stock').value;
		// Collect selected categories
		const checkedCategories = [];
		document.querySelectorAll('#categories-list input[type="checkbox"]').forEach(cb => {
			if (cb.checked) checkedCategories.push(cb.value);
		});
		// Prepare FormData
		const formData = new FormData();
		formData.append('name', name);
		formData.append('description', description);
		formData.append('price', price);
		formData.append('stock', stock);
		checkedCategories.forEach((cat, idx) => {
			formData.append('categories', cat);
		});
		// Append images in order
		imageOrder.forEach((imgIdx, orderIdx) => {
			formData.append('images', images[imgIdx], images[imgIdx].name);
			//formData.append('imageOrder', orderIdx + 1); // optional, for backend
		});
		// Send request
		try {
			const response = await fetch('/api/admin/add-product', {
				method: 'POST',
				body: formData
			});
			const messageDiv = document.getElementById('add-product-message');
			if (response.ok) {
				// Expecting a ProductDto object in response
				const data = await response.json();
				messageDiv.textContent = 'Product added successfully!';
				// Optionally, show product info: messageDiv.textContent += ` (ID: ${data.publicId})`;
				document.getElementById('add-product-form').reset();
				gallery.innerHTML = '';
				images = [];
				imageOrder = [];
				selectedCategories = [];
				renderCategories(categories, selectedCategories);
			} else {
				// Expecting an error object { error, message }
				let errorMsg = 'Failed to add product.';
				try {
					const errObj = await response.json();
					if (errObj && errObj.message) {
						errorMsg = errObj.message;
					}
				} catch (e) {
					// fallback to text if not JSON
					errorMsg = await response.text();
				}
				messageDiv.textContent = 'Failed to add product: ' + errorMsg;
			}
		} catch (error) {
			document.getElementById('add-product-message').textContent = 'Error: ' + error;
		}
	});
});

function renderCategories(categories, selectedCategories) {
	const categoriesListDiv = document.getElementById('categories-list');
	categoriesListDiv.innerHTML = '';
	categories.forEach(category => {
		const label = document.createElement('label');
		label.style.display = 'block';
		const checkbox = document.createElement('input');
		checkbox.type = 'checkbox';
		checkbox.value = category;
		if (selectedCategories.includes(category)) {
			checkbox.checked = true;
		}
		checkbox.addEventListener('change', () => {
			if (checkbox.checked) {
				if (!selectedCategories.includes(category)) selectedCategories.push(category);
			} else {
				selectedCategories = selectedCategories.filter(cat => cat !== category);
			}
		});
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
					// Expecting a success object or empty
					categories = categories.filter(cat => cat !== category);
					selectedCategories = selectedCategories.filter(cat => cat !== category);
					renderCategories(categories, selectedCategories);
					alert(`Category "${category}" deleted successfully!`);
				} else {
					let errorMsg = 'Failed to delete category.';
					try {
						const errObj = await response.json();
						if (errObj && errObj.message) {
							errorMsg = errObj.message;
						}
					} catch (e) {
						errorMsg = await response.text();
					}
					alert(`Failed to delete category: ${errorMsg}`);
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

function renderGallery(images, imageOrder, gallery) {
	gallery.innerHTML = '';
	imageOrder.forEach(idx => {
		const file = images[idx];
		const img = document.createElement('img');
		img.classList.add('draggable-image', 'card', 'gallery-image');
		img.draggable = true;
		img.dataset.idx = idx;
		const reader = new FileReader();
		reader.onload = (e) => {
			img.src = e.target.result;
		};
		reader.readAsDataURL(file);
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
		// Update imageOrder after drag
		updateImageOrder(container);
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

function updateImageOrder(container) {
	imageOrder = Array.from(container.querySelectorAll('img')).map(img => parseInt(img.dataset.idx));
}
