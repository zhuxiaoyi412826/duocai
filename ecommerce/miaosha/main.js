// main.js 电商秒杀活动原型核心交互脚本
// 作者：AI助手
// 功能：倒计时、商品状态切换、库存进度条、弹窗、按钮动效等

// 秒杀活动时间配置
const sessions = [
  {
    tab: 'digital',
    start: new Date(new Date().setHours(10, 0, 0, 0)),
    end: new Date(new Date().setHours(12, 0, 0, 0)),
    products: [
      {
        name: 'iPhone 15 Pro 256G 星光色',
        img: 'https://img14.360buyimg.com/n1/jfs/t1/123456/iphone15pro.jpg',
        oldPrice: 12999,
        promoPrice: 8999,
        stock: 100,
        sold: 82,
        sticker: '限量100件',
        id: 'p1'
      }
    ]
  },
  {
    tab: 'beauty',
    start: new Date(new Date().setHours(15, 0, 0, 0)),
    end: new Date(new Date().setHours(17, 0, 0, 0)),
    products: [
      {
        name: 'YSL 小金条口红',
        img: 'https://img14.360buyimg.com/n1/jfs/t1/beauty.jpg',
        oldPrice: 369,
        promoPrice: 199,
        stock: 50,
        sold: 30,
        sticker: '爆款直降',
        id: 'p2'
      }
    ]
  }
];

let currentTab = 'digital';
let timer = null;

function formatTime(ms) {
  const s = Math.floor(ms / 1000);
  const h = String(Math.floor(s / 3600)).padStart(2, '0');
  const m = String(Math.floor((s % 3600) / 60)).padStart(2, '0');
  const sec = String(s % 60).padStart(2, '0');
  return `${h}:${m}:${sec}`;
}

function renderCountdown() {
  const session = sessions.find(s => s.tab === currentTab);
  const now = new Date();
  let text = '', status = '', btn = '';
  if (now < session.start) {
    const diff = session.start - now;
    text = `距开始还有 ${formatTime(diff)}`;
    status = '未开始';
    btn = '<button class="action-btn prebuy">提醒我</button>';
  } else if (now >= session.start && now < session.end) {
    const diff = session.end - now;
    text = `距结束还有 ${formatTime(diff)}`;
    status = '进行中';
    btn = '<button class="action-btn buying">急速抢购</button>';
  } else {
    text = '活动已结束';
    status = '已结束';
    btn = '<a href="#" class="action-btn soldout">查看相似商品</a>';
  }
  document.getElementById('countdown').innerHTML = text;
  document.getElementById('activityStatus').innerHTML = status + btn;
}

function renderProducts() {
  const session = sessions.find(s => s.tab === currentTab);
  const now = new Date();
  let html = '';
  session.products.forEach(p => {
    let state = '', btn = '', progress = Math.floor((p.sold / p.stock) * 100);
    if (now < session.start) {
      state = '未开始';
      btn = '<button class="action-btn prebuy">提前加购</button>';
    } else if (now >= session.start && now < session.end) {
      if (p.sold >= p.stock) {
        state = '已抢光';
        btn = '<button class="action-btn soldout" disabled>已抢光</button>';
      } else {
        state = '进行中';
        btn = '<button class="action-btn buying">立即抢购</button>';
      }
    } else {
      state = '已结束';
      btn = '<button class="action-btn soldout" disabled>已结束</button>';
    }
    html += `<div class="product-card">
      <span class="sticker">${p.sticker}</span>
      <img class="product-img" src="${p.img}" alt="${p.name}">
      <div class="product-name">${p.name}</div>
      <div class="price-info"><span class="old-price">¥${p.oldPrice}</span><span class="promo-price">¥${p.promoPrice}</span><span class="save-amount">立省${p.oldPrice - p.promoPrice}元</span></div>
      <div class="stock-bar"><div class="stock-bar-inner" style="width:${progress}%"></div></div>
      <div class="stock-text">已抢${progress}%</div>
      ${btn}
    </div>`;
  });
  document.getElementById('productList').innerHTML = html;
}

function switchTab(tab) {
  currentTab = tab;
  document.querySelectorAll('.tab').forEach(btn => btn.classList.remove('active'));
  document.querySelector(`.tab[data-tab="${tab}"]`).classList.add('active');
  renderCountdown();
  renderProducts();
}

function showCouponModal() {
  document.getElementById('couponModal').style.display = 'flex';
}
function hideCouponModal() {
  document.getElementById('couponModal').style.display = 'none';
}

function bindEvents() {
  document.querySelectorAll('.tab').forEach(btn => {
    btn.onclick = () => switchTab(btn.dataset.tab);
  });
  document.getElementById('couponBtn').onclick = showCouponModal;
  document.getElementById('closeCoupon').onclick = hideCouponModal;
  // 按钮音效
  document.body.addEventListener('click', function(e) {
    if (e.target.classList.contains('action-btn')) {
      const audio = new Audio('https://cdn.pixabay.com/audio/2022/07/26/audio_124bfae.mp3');
      audio.play();
    }
  });
}

function startTimer() {
  if (timer) clearInterval(timer);
  timer = setInterval(() => {
    renderCountdown();
    renderProducts();
  }, 1000);
}

window.onload = function() {
  renderCountdown();
  renderProducts();
  bindEvents();
  startTimer();
  // 自动弹出优惠券
  setTimeout(showCouponModal, 800);
};