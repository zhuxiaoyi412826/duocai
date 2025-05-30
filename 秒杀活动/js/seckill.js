// 倒计时功能
function initCountdown() {
    const endTime = new Date();
    endTime.setHours(23, 59, 59); // 设置结束时间为今天23:59:59

    function updateCountdown() {
        const now = new Date();
        const diff = endTime - now;

        if (diff <= 0) {
            document.querySelector('.countdown-timer').innerHTML = '活动已结束';
            return;
        }

        const hours = Math.floor(diff / (1000 * 60 * 60));
        const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((diff % (1000 * 60)) / 1000);

        document.querySelector('.hours').textContent = hours.toString().padStart(2, '0');
        document.querySelector('.minutes').textContent = minutes.toString().padStart(2, '0');
        document.querySelector('.seconds').textContent = seconds.toString().padStart(2, '0');
    }

    updateCountdown();
    setInterval(updateCountdown, 1000);
}

// 购买提醒动画
function showPurchaseNotification() {
    const names = ['张', '李', '王', '刘', '陈', '杨', '赵', '黄', '周', '吴'];
    const products = [
        'iPhone 15 Pro',
        'MacBook Pro',
        'AirPods Pro',
        'iPad Air'
    ];

    function createNotification() {
        const name = names[Math.floor(Math.random() * names.length)] + '**';
        const product = products[Math.floor(Math.random() * products.length)];
        const notification = document.querySelector('.notification-content');
        notification.textContent = `${name} 刚刚抢购了${product}`;
        
        // 添加滑入动画
        notification.style.animation = 'none';
        notification.offsetHeight; // 触发重绘
        notification.style.animation = 'slideIn 0.5s ease-out';
    }

    setInterval(createNotification, 5000);
}

// 模态框控制
function initModals() {
    const modals = document.querySelectorAll('.modal');
    const couponBtn = document.querySelector('.coupon-btn');
    const shareBtn = document.querySelector('.share-btn');
    const closeButtons = document.querySelectorAll('.close-modal');

    function showModal(modalClass) {
        document.querySelector(modalClass).style.display = 'flex';
    }

    function hideModal(modal) {
        modal.style.display = 'none';
    }

    couponBtn.addEventListener('click', () => showModal('.coupon-modal'));
    shareBtn.addEventListener('click', () => showModal('.share-modal'));

    closeButtons.forEach(button => {
        button.addEventListener('click', () => {
            hideModal(button.closest('.modal'));
        });
    });

    modals.forEach(modal => {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                hideModal(modal);
            }
        });
    });
}

// 商品库存进度条动画
function initProgressBars() {
    const progressBars = document.querySelectorAll('.progress');
    progressBars.forEach(bar => {
        const width = bar.style.width;
        bar.style.width = '0';
        setTimeout(() => {
            bar.style.transition = 'width 1s ease-out';
            bar.style.width = width;
        }, 100);
    });
}

// 抢购按钮状态控制
function initBuyButtons() {
    const buyButtons = document.querySelectorAll('.buy-button');
    buyButtons.forEach(button => {
        button.addEventListener('click', function() {
            if (!document.querySelector('.login-btn').classList.contains('logged-in')) {
                showLoginModal();
                return;
            }
            
            this.disabled = true;
            this.textContent = '抢购中...';
            
            // 模拟抢购过程
            setTimeout(() => {
                const success = Math.random() > 0.5;
                if (success) {
                    this.textContent = '抢购成功';
                    this.classList.add('success');
                    showSuccessModal();
                } else {
                    this.textContent = '已抢完';
                    this.classList.add('sold-out');
                }
            }, 2000);
        });
    });
}

// 标签页切换
function initTabs() {
    const tabs = document.querySelectorAll('.tab');
    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            tabs.forEach(t => t.classList.remove('active'));
            this.classList.add('active');
            // 这里可以添加加载对应场次商品的逻辑
        });
    });
}

// 登录状态模拟
function initLoginButton() {
    const loginBtn = document.querySelector('.login-btn');
    loginBtn.addEventListener('click', function() {
        this.classList.toggle('logged-in');
        this.textContent = this.classList.contains('logged-in') ? '已登录' : '快速登录';
    });
}

// 页面加载完成后初始化所有功能
document.addEventListener('DOMContentLoaded', () => {
    initCountdown();
    showPurchaseNotification();
    initModals();
    initProgressBars();
    initBuyButtons();
    initTabs();
    initLoginButton();
});

// 防止重复点击
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// 添加页面滚动动画
function initScrollAnimation() {
    const productCards = document.querySelectorAll('.product-card');
    const observer = new IntersectionObserver(
        (entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('fade-in');
                }
            });
        },
        { threshold: 0.1 }
    );

    productCards.forEach(card => {
        observer.observe(card);
    });
}

// 初始化页面滚动动画
initScrollAnimation();

// 移动端菜单处理
function initMobileMenu() {
    const mobileMenuBtn = document.getElementById('mobileMenuBtn');
    const navMenu = document.getElementById('navMenu');
    
    if (mobileMenuBtn && navMenu) {
        // 点击菜单按钮切换菜单显示状态
        mobileMenuBtn.addEventListener('click', function(event) {
            event.stopPropagation();
            toggleMenu();
        });

        // 点击菜单外区域关闭菜单
        document.addEventListener('click', function(event) {
            if (!navMenu.contains(event.target) && !mobileMenuBtn.contains(event.target)) {
                closeMenu();
            }
        });

        // 窗口调整大小时处理
        const handleResize = debounce(function() {
            if (window.innerWidth > 768) {
                closeMenu();
            }
        }, 250);

        window.addEventListener('resize', handleResize);

        // 为菜单项添加点击处理
        const menuItems = navMenu.querySelectorAll('a');
        menuItems.forEach(item => {
            item.addEventListener('click', () => {
                closeMenu();
            });
        });

        // 添加键盘导航支持
        mobileMenuBtn.addEventListener('keydown', function(event) {
            // 按下Enter或Space键时切换菜单
            if (event.key === 'Enter' || event.key === ' ') {
                event.preventDefault();
                toggleMenu();
            }
        });

        navMenu.addEventListener('keydown', function(event) {
            // ESC键关闭菜单
            if (event.key === 'Escape') {
                closeMenu();
                mobileMenuBtn.focus();
            }
            
            // 箭头键导航
            if (event.key === 'ArrowDown' || event.key === 'ArrowUp') {
                event.preventDefault();
                
                const menuLinks = Array.from(navMenu.querySelectorAll('a'));
                const currentIndex = menuLinks.indexOf(document.activeElement);
                let nextIndex;
                
                if (event.key === 'ArrowDown') {
                    nextIndex = currentIndex < menuLinks.length - 1 ? currentIndex + 1 : 0;
                } else {
                    nextIndex = currentIndex > 0 ? currentIndex - 1 : menuLinks.length - 1;
                }
                
                menuLinks[nextIndex].focus();
            }
        });

        // 初始化菜单项动画延迟
        function initMenuItemsDelay() {
            const menuItems = navMenu.querySelectorAll('li');
            menuItems.forEach((item, index) => {
                item.style.setProperty('--item-index', index);
            });
        }

        // 辅助函数：切换菜单
        function toggleMenu() {
            // 添加加载状态
            navMenu.classList.add('loading');
            
            // 使用 requestAnimationFrame 确保动画流畅
            requestAnimationFrame(() => {
                const isExpanded = navMenu.classList.toggle('show');
                mobileMenuBtn.setAttribute('aria-expanded', isExpanded);
                
                if (isExpanded) {
                    // 打开菜单时，焦点移到第一个菜单项
                    const firstMenuItem = navMenu.querySelector('a');
                    if (firstMenuItem) {
                        setTimeout(() => {
                            firstMenuItem.focus();
                            // 移除加载状态
                            navMenu.classList.remove('loading');
                        }, 300); // 等待过渡动画完成
                    }
                } else {
                    // 关闭时也需要移除加载状态
                    setTimeout(() => {
                        navMenu.classList.remove('loading');
                    }, 300);
                }
            });
        }

        // 辅助函数：关闭菜单
        function closeMenu() {
            if (navMenu.classList.contains('show')) {
                navMenu.classList.add('loading');
                requestAnimationFrame(() => {
                    navMenu.classList.remove('show');
                    mobileMenuBtn.setAttribute('aria-expanded', 'false');
                    setTimeout(() => {
                        navMenu.classList.remove('loading');
                    }, 300);
                });
            }
        }

        // 性能优化：使用 Intersection Observer 延迟加载菜单项
        const menuItemsObserver = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('visible');
                    menuItemsObserver.unobserve(entry.target);
                }
            });
        }, {
            threshold: 0.1
        });

        // 观察菜单项
        navMenu.querySelectorAll('li').forEach(item => {
            menuItemsObserver.observe(item);
        });

        // 初始化菜单项延迟
        initMenuItemsDelay();

        // 添加触摸事件支持
        let touchStartY = 0;
        navMenu.addEventListener('touchstart', (e) => {
            touchStartY = e.touches[0].clientY;
        }, { passive: true });

        navMenu.addEventListener('touchmove', (e) => {
            const touchY = e.touches[0].clientY;
            const scrollTop = navMenu.scrollTop;
            const scrollHeight = navMenu.scrollHeight;
            const clientHeight = navMenu.clientHeight;

            // 防止菜单滚动到顶部或底部时页面滚动
            if ((scrollTop <= 0 && touchY > touchStartY) ||
                (scrollTop + clientHeight >= scrollHeight && touchY < touchStartY)) {
                e.preventDefault();
            }
        }, { passive: false });
    }
}

// 在页面加载完成后初始化所有功能
document.addEventListener('DOMContentLoaded', () => {
    initCountdown();
    showPurchaseNotification();
    initModals();
    initProgressBars();
    initBuyButtons();
    initTabs();
    initLoginButton();
    initMobileMenu(); // 添加移动端菜单初始化
});