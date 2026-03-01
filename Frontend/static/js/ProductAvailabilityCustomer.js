const PRODUCT_BASE_URL = "http://localhost:8080/api/v1/product/customer";
const CART_BASE_URL = "http://localhost:8080/api/cart";

let allProducts = [];

$(document).ready(function () {
    fetchProducts();
    getCart();
});

function fetchProducts() {
    $.ajax({
        url: `${PRODUCT_BASE_URL}/getAllProducts`,
        method: "GET",
        success: function (response) {
            if (response && response.content && Array.isArray(response.content)) {
                allProducts = response.content;
                displayProducts(allProducts);
            } else {
                allProducts = [];
                displayProducts([]);
            }
        },
        error: (xhr) => showNotification(`Error fetching products: ${xhr.statusText}`, "error"),
    });
}

function displayProducts(products) {
    const grid = $("#productsGrid");
    grid.empty();

    if (!products || products.length === 0) {
        const emptyState = `
            <div class="empty-state">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-5.197-5.197m0 0A7.5 7.5 0 105.196 5.196a7.5 7.5 0 0010.607 10.607z" /></svg>
                <h3>No Products Found</h3>
                <p>There are no products matching your current search or filter.</p>
            </div>`;
        grid.html(emptyState);
        return;
    }

    products.forEach(p => {
        const productCard = `
            <div class="product-card">
                <div>
                    <div class="product-header">
                        <div>
                            <div class="product-name">${p.productName}</div>
                            <div class="product-category">${p.productCategory}</div>
                        </div>
                        ${getStatusBadge(p.productStatus)}
                    </div>
                    <div class="product-price">Rs. ${parseFloat(p.productPrice).toFixed(2)}</div>
                </div>
                <div class="product-actions">
                     <button class="btn btn-success" style="width: 100%;" 
                        onclick="addToCart(${p.productId})"
                        ${p.productStatus === 'OUT_OF_STOCK' ? 'disabled' : ''}>
                        <i class="bi bi-cart-plus-fill"></i> Add to Cart
                     </button>
                </div>
            </div>
        `;
        grid.append(productCard);
    });
}

function getStatusBadge(status) {
    const statuses = {
        "IN_STOCK": `<span class="status-badge status-in-stock">In Stock</span>`,
        "AVAILABLE": `<span class="status-badge status-available">Low Stock</span>`,
        "OUT_OF_STOCK": `<span class="status-badge status-out">Out of Stock</span>`,
    };
    return statuses[status] || `<span class="status-badge">${status || 'N/A'}</span>`;
}

function getCart() {
    $.ajax({
        url: CART_BASE_URL,
        method: "GET",
        success: (response) => updateCartDisplay(response),
        error: (xhr) => showNotification(`Error fetching cart: ${xhr.statusText}`, "error"),
    });
}

function addToCart(productId) {
    const requestData = { productId: productId, quantity: 1 };
    $.ajax({
        url: `${CART_BASE_URL}/add`,
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify(requestData),
        success: (response) => {
            showNotification("Product added to cart!", "success");
            updateCartDisplay(response);
        },
        error: (xhr) => showNotification(`Error: ${xhr.responseText || 'Could not add to cart.'}`, "error"),
    });
}

function updateQuantity(productId) {
    const newQuantity = parseInt($(`#quantity-input-${productId}`).val(), 10);
    if (newQuantity <= 0) {
        removeFromCart(productId);
        return;
    }
    $.ajax({
        url: `${CART_BASE_URL}/update/${productId}?quantity=${newQuantity}`,
        method: "PUT",
        success: (response) => {
            showNotification("Cart updated.", "success");
            updateCartDisplay(response);
        },
        error: (xhr) => showNotification(`Error: ${xhr.responseText || 'Could not update quantity.'}`, "error"),
    });
}

function removeFromCart(productId) {
    $.ajax({
        url: `${CART_BASE_URL}/remove/${productId}`,
        method: "DELETE",
        success: (response) => {
            showNotification("Item removed from cart.", "success");
            updateCartDisplay(response);
        },
        error: (xhr) => showNotification(`Error: ${xhr.responseText || 'Could not remove item.'}`, "error"),
    });
}

function clearCart() {
    if (!confirm("Are you sure you want to clear your entire cart?")) return;
    $.ajax({
        url: `${CART_BASE_URL}/clear`,
        method: "DELETE",
        success: () => {
            showNotification("Cart cleared.", "success");
            getCart();
        },
        error: (xhr) => showNotification(`Error: ${xhr.responseText || 'Could not clear cart.'}`, "error"),
    });
}

function updateCartDisplay(cartData) {
    const { items, totalPrice, totalItems } = cartData;
    const container = $("#cartItemsContainer");

    $("#cartCount").text(totalItems);

    container.empty();

    if (!items || items.length === 0) {
        container.html('<div class="cart-empty-msg"><h3>Your Cart is Empty</h3></div>');
        $("#cartTotal").text("Rs. 0.00");
        return;
    }

    items.forEach(item => {
        const cartItemHtml = `
            <div class="cart-item">
                <div class="cart-item-details">
                    <div class="cart-item-name">${item.productName}</div>
                    <div class="cart-item-price">Rs. ${item.unitPrice.toFixed(2)} each</div>
                </div>
                <div class="cart-item-quantity">
                    <input type="number" class="form-control form-control-sm" id="quantity-input-${item.productId}" 
                           value="${item.quantity}" min="1" onchange="updateQuantity(${item.productId})">
                </div>
                <div class="cart-item-subtotal">Rs. ${item.subtotal.toFixed(2)}</div>
                <button class="btn btn-sm btn-outline-danger" onclick="removeFromCart(${item.productId})"><i class="bi bi-trash"></i></button>
            </div>
        `;
        container.append(cartItemHtml);
    });

    $("#cartTotal").text(`Rs. ${totalPrice.toFixed(2)}`);
}

function openCartModal() { $("#cartModal").addClass('active'); }
function closeCartModal() { $("#cartModal").removeClass('active'); }

function searchProducts() {
    const query = $("#searchInput").val().trim();
    if (!query) {
        displayProducts(allProducts);
        return;
    }
    $.ajax({
        url: `${PRODUCT_BASE_URL}/searchProduct?name=${encodeURIComponent(query)}`,
        method: "GET",
        success: (response) => displayProducts(response.content ? [response.content] : []),
        error: () => displayProducts([]),
    });
}

function filterData(type, value = null) {
    $('.category-btn').removeClass('active');
    const activeBtn = (type === 'Category') ? $(`.category-btn:contains('${value}')`) : $(`.category-btn:contains('${type === 'All' ? 'All Products' : 'Available Only'}')`);
    activeBtn.addClass('active');

    let filtered = [];
    if (type === 'All') {
        filtered = allProducts;
    } else if (type === 'Available') {
        filtered = allProducts.filter(p => p.productStatus !== 'OUT_OF_STOCK');
    } else if (type === 'Category') {
        filtered = allProducts.filter(p => p.productCategory === value);
    }
    displayProducts(filtered);
}

function handleEnterKey(event) { if (event.key === "Enter") searchProducts(); }

function showNotification(message, type) {
    const notification = $(`<div class="notification ${type}">${message}</div>`);
    $('body').append(notification);
    setTimeout(() => notification.remove(), 3000);
}

