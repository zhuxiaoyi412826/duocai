// 电商网站交互功能

document.addEventListener('DOMContentLoaded', function() {
    // 初始化工具提示
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // 初始化轮播图
    var carouselElement = document.querySelector('#mainCarousel');
    if (carouselElement) {
        var carousel = new bootstrap.Carousel(carouselElement, {
            interval: 5000,
            wrap: true
        });
    }

    // 商品筛选功能
    const filterButtons = document.querySelectorAll('.filter-btn');
    if (filterButtons.length > 0) {
        filterButtons.forEach(button => {
            button.addEventListener('click', function() {
                const filterValue = this.getAttribute('data-filter');
                
                // 移除所有按钮的active类
                filterButtons.forEach(btn => btn.classList.remove('active'));
                
                // 添加active类到当前按钮
                this.classList.add('active');
                
                // 筛选商品
                const productItems = document.querySelectorAll('.product-item');
                productItems.forEach(item => {
                    if (filterValue === 'all') {
                        item.style.display = 'block';
                    } else {
                        if (item.classList.contains(filterValue)) {
                            item.style.display = 'block';
                        } else {
                            item.style.display = 'none';
                        }
                    }
                });
            });
        });
    }

    // 购物车功能
    const addToCartButtons = document.querySelectorAll('.add-to-cart');
    if (addToCartButtons.length > 0) {
        addToCartButtons.forEach(button => {
            button.addEventListener('click', function(e) {
                e.preventDefault();
                
                const productId = this.getAttribute('data-product-id');
                const productName = this.getAttribute('data-product-name');
                const productPrice = this.getAttribute('data-product-price');
                const productImage = this.getAttribute('data-product-image');
                
                // 添加到购物车
                addToCart(productId, productName, productPrice, productImage);
                
                // 显示提示
                showNotification(`${productName} 已添加到购物车`);
                
                // 更新购物车计数
                updateCartCount();
            });
        });
    }

    // 数量增减功能
    const quantityInputs = document.querySelectorAll('.quantity-input');
    if (quantityInputs.length > 0) {
        quantityInputs.forEach(input => {
            const decrementBtn = input.previousElementSibling;
            const incrementBtn = input.nextElementSibling;
            
            decrementBtn.addEventListener('click', function() {
                if (input.value > 1) {
                    input.value = parseInt(input.value) - 1;
                    updateCartItemTotal(input);
                }
            });
            
            incrementBtn.addEventListener('click', function() {
                input.value = parseInt(input.value) + 1;
                updateCartItemTotal(input);
            });
            
            input.addEventListener('change', function() {
                if (input.value < 1 || isNaN(input.value)) {
                    input.value = 1;
                }
                updateCartItemTotal(input);
            });
        });
    }

    // 移除购物车商品
    const removeCartItemButtons = document.querySelectorAll('.remove-cart-item');
    if (removeCartItemButtons.length > 0) {
        removeCartItemButtons.forEach(button => {
            button.addEventListener('click', function() {
                const cartItem = this.closest('.cart-item');
                const productId = this.getAttribute('data-product-id');
                
                // 从DOM中移除
                cartItem.remove();
                
                // 从存储中移除
                removeFromCart(productId);
                
                // 更新购物车总计
                updateCartTotal();
                
                // 更新购物车计数
                updateCartCount();
            });
        });
    }

    // 表单验证
    const forms = document.querySelectorAll('.needs-validation');
    if (forms.length > 0) {
        Array.from(forms).forEach(form => {
            form.addEventListener('submit', function(event) {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                
                form.classList.add('was-validated');
            }, false);
        });
    }

    // 评分系统
    const ratingInputs = document.querySelectorAll('.rating-input');
    if (ratingInputs.length > 0) {
        ratingInputs.forEach(input => {
            input.addEventListener('change', function() {
                const ratingValue = this.value;
                const ratingStars = this.parentElement.querySelectorAll('.rating-star');
                
                ratingStars.forEach((star, index) => {
                    if (index < ratingValue) {
                        star.classList.add('active');
                    } else {
                        star.classList.remove('active');
                    }
                });
            });
        });
    }

    // 图片预览
    const productThumbnails = document.querySelectorAll('.product-thumbnail');
    if (productThumbnails.length > 0) {
        productThumbnails.forEach(thumbnail => {
            thumbnail.addEventListener('click', function() {
                const mainImage = document.querySelector('.product-main-image');
                const thumbnailSrc = this.getAttribute('src');
                
                // 移除所有缩略图的active类
                productThumbnails.forEach(thumb => thumb.classList.remove('active'));
                
                // 添加active类到当前缩略图
                this.classList.add('active');
                
                // 更新主图
                mainImage.setAttribute('src', thumbnailSrc);
            });
        });
    }
});

// 辅助函数

// 添加到购物车
function addToCart(id, name, price, image) {
    let cart = JSON.parse(localStorage.getItem('cart')) || [];
    
    // 检查商品是否已在购物车中
    const existingItem = cart.find(item => item.id === id);
    
    if (existingItem) {
        existingItem.quantity += 1;
    } else {
        cart.push({
            id: id,
            name: name,
            price: price,
            image: image,
            quantity: 1
        });
    }
    
    // 保存到本地存储
    localStorage.setItem('cart', JSON.stringify(cart));
}

// 从购物车移除
function removeFromCart(id) {
    let cart = JSON.parse(localStorage.getItem('cart')) || [];
    cart = cart.filter(item => item.id !== id);
    localStorage.setItem('cart', JSON.stringify(cart));
}

// 更新购物车计数
function updateCartCount() {
    const cart = JSON.parse(localStorage.getItem('cart')) || [];
    const count = cart.reduce((total, item) => total + item.quantity, 0);
    
    const cartCountElements = document.querySelectorAll('.cart-count');
    cartCountElements.forEach(element => {
        element.textContent = count;
    });
}

// 更新购物车商品总价
function updateCartItemTotal(input) {
    const cartItem = input.closest('.cart-item');
    const quantity = parseInt(input.value);
    const price = parseFloat(cartItem.querySelector('.cart-item-price').getAttribute('data-price'));
    const totalElement = cartItem.querySelector('.cart-item-total');
    
    const total = quantity * price;
    totalElement.textContent = `¥${total.toFixed(2)}`;
    
    // 更新购物车总计
    updateCartTotal();
    
    // 更新本地存储中的数量
    const productId = cartItem.getAttribute('data-product-id');
    updateCartItemQuantity(productId, quantity);
}

// 更新购物车商品数量
function updateCartItemQuantity(id, quantity) {
    let cart = JSON.parse(localStorage.getItem('cart')) || [];
    const item = cart.find(item => item.id === id);
    
    if (item) {
        item.quantity = quantity;
        localStorage.setItem('cart', JSON.stringify(cart));
    }
}

// 更新购物车总计
function updateCartTotal() {
    const cart = JSON.parse(localStorage.getItem('cart')) || [];
    const subtotal = cart.reduce((total, item) => total + (parseFloat(item.price) * item.quantity), 0);
    const shipping = subtotal > 0 ? 10 : 0; // 假设运费为10元
    const total = subtotal + shipping;
    
    const subtotalElement = document.querySelector('.cart-subtotal');
    const shippingElement = document.querySelector('.cart-shipping');
    const totalElement = document.querySelector('.cart-total');
    
    if (subtotalElement) subtotalElement.textContent = `¥${subtotal.toFixed(2)}`;
    if (shippingElement) shippingElement.textContent = `¥${shipping.toFixed(2)}`;
    if (totalElement) totalElement.textContent = `¥${total.toFixed(2)}`;
}

// 显示通知
function showNotification(message) {
    const notification = document.createElement('div');
    notification.className = 'notification';
    notification.textContent = message;
    
    document.body.appendChild(notification);
    
    // 显示通知
    setTimeout(() => {
        notification.classList.add('show');
    }, 10);
    
    // 3秒后隐藏通知
    setTimeout(() => {
        notification.classList.remove('show');
        
        // 动画结束后移除元素
        notification.addEventListener('transitionend', function() {
            notification.remove();
        });
    }, 3000);
}

// 页面加载时初始化购物车计数
window.addEventListener('load', function() {
    updateCartCount();
    
    // 如果在购物车页面，初始化购物车
    const cartContainer = document.querySelector('.cart-items-container');
    if (cartContainer) {
        initializeCart();
    }
});

// 初始化购物车
function initializeCart() {
    const cart = JSON.parse(localStorage.getItem('cart')) || [];
    const cartContainer = document.querySelector('.cart-items-container');
    
    if (cartContainer) {
        if (cart.length === 0) {
            cartContainer.innerHTML = '<div class="alert alert-info">您的购物车是空的</div>';
        } else {
            cartContainer.innerHTML = '';
            
            cart.forEach(item => {
                const cartItemHTML = `
                    <div class="cart-item" data-product-id="${item.id}">
                        <div class="row align-items-center">
                            <div class="col-md-2">
                                <img src="${item.image}" alt="${item.name}" class="cart-item-image">
                            </div>
                            <div class="col-md-4">
                                <h5 class="cart-item-title">${item.name}</h5>
                            </div>
                            <div class="col-md-2">
                                <span class="cart-item-price" data-price="${item.price}">¥${item.price}</span>
                            </div>
                            <div class="col-md-2">
                                <div class="quantity-control">
                                    <button class="btn btn-sm btn-outline-secondary quantity-btn">-</button>
                                    <input type="number" class="form-control quantity-input" value="${item.quantity}" min="1">
                                    <button class="btn btn-sm btn-outline-secondary quantity-btn">+</button>
                                </div>
                            </div>
                            <div class="col-md-1">
                                <span class="cart-item-total">¥${(item.price * item.quantity).toFixed(2)}</span>
                            </div>
                            <div class="col-md-1">
                                <button class="btn btn-sm btn-danger remove-cart-item" data-product-id="${item.id}">
                                    <i class="bi bi-trash"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                `;
                
                cartContainer.innerHTML += cartItemHTML;
            });
            
            // 重新绑定事件
            const quantityInputs = document.querySelectorAll('.quantity-input');
            quantityInputs.forEach(input => {
                const decrementBtn = input.previousElementSibling;
                const incrementBtn = input.nextElementSibling;
                
                decrementBtn.addEventListener('click', function() {
                    if (input.value > 1) {
                        input.value = parseInt(input.value) - 1;
                        updateCartItemTotal(input);
                    }
                });
                
                incrementBtn.addEventListener('click', function() {
                    input.value = parseInt(input.value) + 1;
                    updateCartItemTotal(input);
                });
                
                input.addEventListener('change', function() {
                    if (input.value < 1 || isNaN(input.value)) {
                        input.value = 1;
                    }
                    updateCartItemTotal(input);
                });
            });
            
            const removeCartItemButtons = document.querySelectorAll('.remove-cart-item');
            removeCartItemButtons.forEach(button => {
                button.addEventListener('click', function() {
                    const cartItem = this.closest('.cart-item');
                    const productId = this.getAttribute('data-product-id');
                    
                    // 从DOM中移除
                    cartItem.remove();
                    
                    // 从存储中移除
                    removeFromCart(productId);
                    
                    // 更新购物车总计
                    updateCartTotal();
                    
                    // 更新购物车计数
                    updateCartCount();
                    
                    // 如果购物车为空，显示提示
                    if (document.querySelectorAll('.cart-item').length === 0) {
                        cartContainer.innerHTML = '<div class="alert alert-info">您的购物车是空的</div>';
                    }
                });
            });
        }
        
        // 更新购物车总计
        updateCartTotal();
    }
}