const BASE_URL = "http://localhost:8080/api/v1/product/admin";
let allProducts = [];

$(document).ready(function () {
    fetchProducts();
});

// --- Core Functions ---

function fetchProducts() {
    $.ajax({
        url: `${BASE_URL}/getAllProducts`,
        method: "GET",
        xhrFields: { withCredentials: true },
        success: function (response) {
            if (response && response.content) {
                allProducts = response.content;
                displayProducts(allProducts);
            } else {
                displayProducts([]);
            }
        },
        error: function (xhr) {
            showNotification(`Error fetching products: ${xhr.statusText}`, "error");
            displayProducts([]);
        },
    });
}

function displayProducts(products) {
    const grid = $("#productsGrid");
    grid.empty();

    if (!products || products.length === 0) {
        grid.html(`
            <div class="empty-state">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 14.25v-2.625a3.375 3.375 0 00-3.375-3.375h-1.5A1.125 1.125 0 0113.5 7.125v-1.5a3.375 3.375 0 00-3.375-3.375H8.25m0 12.75h7.5m-7.5 3H12M10.5 2.25H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 00-9-9z" />
                </svg>
                <h3>No Products Found</h3>
            </div>
        `);
        return;
    }

    products.forEach(p => {
        const expiryDateStr = p.expiryDate ? new Date(p.expiryDate).toLocaleDateString() : 'N/A';

        const productCard = `
            <div class="product-card">
                <div class="product-header">
                    <div>
                        <div class="product-name">${p.productName}</div>
                        <div class="product-category">${p.productCategory}</div>
                    </div>
                    ${getStatusBadge(p.productStatus)}
                </div>
                <div class="product-price">Rs. ${parseFloat(p.productPrice).toFixed(2)}</div>
                <div class="product-stock">
                    Stock: <strong>${p.productQuantity}</strong> | Low Stock at: <strong>${p.lowStockThreshold}</strong>
                </div>
                <div class="product-stock">
                    Expires on: <strong>${expiryDateStr}</strong>
                </div>
                <div class="product-actions">
                    <button class="btn btn-small btn-edit" onclick="openModal('edit', ${p.productId})"><i class="bi bi-pencil-fill">  </i>Edit</button>
                    <button class="btn btn-small btn-danger" onclick="deleteProduct(${p.productId})"><i class="bi bi-trash">  </i>Delete</button>
                </div>
            </div>
        `;
        grid.append(productCard);
    });
}

function getStatusBadge(status) {
    switch (status) {
        case "IN_STOCK": return `<span class="status-badge status-in-stock">In Stock</span>`;
        case "AVAILABLE": return `<span class="status-badge status-available">Low Stock</span>`;
        case "OUT_OF_STOCK": return `<span class="status-badge status-out">Out of Stock</span>`;
        default: return `<span class="status-badge">${status || 'N/A'}</span>`;
    }
}

// --- Modal Management (Add & Edit) ---

function openModal(mode, productId = null) {
    $('#productForm').trigger("reset");
    $('#productId').val('');

    if (mode === 'edit') {
        const product = allProducts.find(p => p.productId === productId);
        if (product) {
            $('#modalTitle').text('Edit Product');
            $('#productId').val(product.productId);
            $('#productName').val(product.productName);
            $('#productPrice').val(product.productPrice);
            $('#productCategory').val(product.productCategory);
            $('#productQuantity').val(product.productQuantity);
            $('#lowStockThreshold').val(product.lowStockThreshold);

            // --- MODIFICATION: Populate new expiry fields when editing ---
            $('#expiryDate').val(product.expiryDate);
            $('#expiryThresholdDays').val(product.expiryThresholdDays);

        } else {
            showNotification(`Product with ID ${productId} not found.`, "error");
            return;
        }
    } else {
        $('#modalTitle').text('Add New Product');
    }

    $('#productModal').addClass('active');
}

function closeModal() {
    $('#productModal').removeClass('active');
}

// --- CRUD Operations ---

function saveProduct() {
    const productId = $('#productId').val();
    const productData = {
        // Original fields
        productName: $('#productName').val(),
        productPrice: parseFloat($('#productPrice').val()),
        productCategory: $('#productCategory').val(),
        productQuantity: parseInt($('#productQuantity').val(), 10),
        lowStockThreshold: parseInt($('#lowStockThreshold').val(), 10),

        // --- MODIFICATION: Read new expiry fields from the form ---
        expiryDate: $('#expiryDate').val(),
        expiryThresholdDays: parseInt($('#expiryThresholdDays').val(), 10)
    };

    const isUpdate = !!productId;
    const url = isUpdate ? `${BASE_URL}/updateProduct` : `${BASE_URL}/saveProduct`;
    const method = isUpdate ? "PUT" : "POST";

    if (isUpdate) {
        productData.productId = parseInt(productId, 10);
    }

    $.ajax({
        url: url,
        method: method,
        contentType: "application/json",
        data: JSON.stringify(productData),
        xhrFields: { withCredentials: true },
        success: function () {
            showNotification(`Product ${isUpdate ? 'updated' : 'saved'} successfully!`, 'success');
            closeModal();
            fetchProducts();
        },
        error: function (xhr) {
            const errorMsg = xhr.responseJSON?.message || xhr.statusText || "An error occurred.";
            showNotification(`Error: ${errorMsg}`, "error");
        },
    });
}

function deleteProduct(id) {
    if (!confirm('Are you sure you want to delete this product?')) {
        return;
    }
    $.ajax({
        url: `${BASE_URL}/deleteProduct/${id}`,
        method: "DELETE",
        xhrFields: { withCredentials: true },
        success: function () {
            showNotification("Product deleted successfully!", "warning");
            fetchProducts();
        },
        error: function () {
            showNotification("Error deleting product.", "error");
        },
    });
}

// --- Search & Filter ---

function searchProducts() {
    const query = $("#searchInput").val().trim().toLowerCase();
    if (!query) {
        displayProducts(allProducts); // If search is empty, show all products
        return;
    }
    const filtered = allProducts.filter(p => p.productName.toLowerCase().includes(query));
    displayProducts(filtered);
}

function filterByCategory(category) {
    $('.category-btn').removeClass('active');
    // A more reliable way to select the button
    $('.category-btn').filter(function() {
        return $(this).text() === category;
    }).addClass('active');

    if (category === 'All') {
        displayProducts(allProducts);
        return;
    }
    const filtered = allProducts.filter(p => p.productCategory === category);
    displayProducts(filtered);
}

// --- Utilities & Event Handlers ---

function handleEnterKey(event) {
    if (event.key === "Enter") {
        searchProducts();
    }
}

function showNotification(message, type) {
    const notification = $(`<div class="notification ${type}">${message}</div>`);
    $('body').append(notification);
    setTimeout(() => {
        notification.remove();
    }, 3000);
}