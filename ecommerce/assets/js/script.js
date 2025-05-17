// 商品数据库
const productsDatabase = {
    electronics: [
        {id:1, name:"智能手机X Pro", price:3999, image:"assets/images/product1.svg", sales:1250},
        {id:2, name:"无线降噪耳机", price:599, image:"assets/images/product2.svg", sales:892},
        {id:3, name:"智能手表", price:1299, image:"assets/images/product3.svg", sales:456}
    ],
    phone: [
        {id:4, name:"旗舰智能手机", price:5999, image:"assets/images/product4.svg", sales:320},
        {id:5, name:"拍照手机", price:2999, image:"assets/images/product1.svg", sales:780}
    ],
    clothing: [
        {id:6, name:"男士休闲衬衫", price:199, image:"assets/images/product2.svg", sales:1200},
        {id:7, name:"女士连衣裙", price:299, image:"assets/images/product3.svg", sales:950}
    ],
    // 其他分类数据...
};

// 初始化分类页面
function initCategoryPage() {
    if(!document.getElementById('categoryTree')) return;
    
    // 绑定分类点击事件
    document.querySelectorAll('.category-item').forEach(item => {
        item.addEventListener('click', function(e) {
            // 阻止子项点击冒泡
            if(e.target !== this && this.contains(e.target)) return;
            
            // 切换分类激活状态
            const parentItem = this.closest('.category-item');
            if(parentItem) {
                parentItem.classList.toggle('active');
            }
            
            // 加载商品
            const category = this.dataset.category;
            if(category) loadProducts(category);
        });
    });
    
    // 默认加载第一个分类
    const firstCategory = document.querySelector('.category-item[data-category]');
    if(firstCategory) {
        loadProducts(firstCategory.dataset.category);
    }
}

// 加载商品列表
function loadProducts(category) {
    const products = productsDatabase[category] || [];
    const grid = document.getElementById('productGrid');
    
    // 更新当前分类标题
    const currentCategory = document.querySelector(`[data-category="${category}"]`);
    if(currentCategory) {
        document.querySelector('#currentCategory span').textContent = 
            currentCategory.textContent.trim();
        document.querySelector('#currentCategory small').textContent = 
            `共${products.length}件商品`;
    }
    
    // 生成商品卡片
    grid.innerHTML = products.map(product => `
        <div class="col-xl-3 col-lg-4 col-md-6">
            <div class="card h-100 product-card">
                <div class="position-relative">
                    <img src="${product.image}" class="card-img-top" alt="${product.name}">
                    ${product.sales > 800 ? 
                        `<span class="badge bg-danger position-absolute top-0 start-0 m-2">热销</span>` : ''}
                </div>
                <div class="card-body">
                    <h5 class="card-title">${product.name}</h5>
                    <div class="d-flex justify-content-between align-items-center mt-3">
                        <div>
                            <span class="price">¥${product.price}</span>
                            ${product.originalPrice ? 
                                `<small class="text-muted ms-2 text-decoration-line-through">
                                    ¥${product.originalPrice}
                                </small>` : ''}
                        </div>
                        <button class="btn btn-sm btn-primary add-to-cart"
                            data-product-id="${product.id}"
                            data-product-name="${product.name}"
                            data-product-price="${product.price}"
                            data-product-image="${product.image}">
                            <i class="bi bi-cart-plus"></i>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `).join('');
    
    // 重新绑定购物车事件
    bindCartEvents();
}

// 绑定购物车事件
function bindCartEvents() {
    document.querySelectorAll('.add-to-cart').forEach(btn => {
        btn.addEventListener('click', function() {
            const product = {
                id: this.dataset.productId,
                name: this.dataset.productName,
                price: this.dataset.productPrice,
                image: this.dataset.productImage
            };
            
            let cart = JSON.parse(localStorage.getItem('cart')) || [];
            cart.push(product);
            localStorage.setItem('cart', JSON.stringify(cart));
            
            // 显示添加成功提示
            showToast(`${product.name} 已加入购物车`);
            updateCartCount();
        });
    });
}

// 显示提示信息
function showToast(message) {
    const toast = document.createElement('div');
    toast.className = 'position-fixed bottom-0 end-0 m-3 toast show';
    toast.innerHTML = `
        <div class="toast-body bg-success text-white rounded">
            <i class="bi bi-check-circle me-2"></i>
            ${message}
        </div>
    `;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}

// 更新购物车数量
function updateCartCount() {
    const cart = JSON.parse(localStorage.getItem('cart')) || [];
    document.querySelectorAll('.cart-count').forEach(el => {
        el.textContent = cart.length;
    });
}

// 页面初始化
document.addEventListener('DOMContentLoaded', () => {
    initCategoryPage();
    updateCartCount();
    
    // 处理URL参数
    const params = new URLSearchParams(location.search);
    if(params.has('category')) {
        loadProducts(params.get('category'));
    }
});