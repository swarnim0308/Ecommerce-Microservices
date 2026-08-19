import './style.css'

const app = document.querySelector('#app')

const state = {
  user: JSON.parse(localStorage.getItem('user')) || null
}

// Backend service endpoints, proxied to the API Gateway on :8080.
// The gateway rewrites the /api/<service>/ prefix to each service's controller.
const API = {
  products: '/api/productservice',
  customers: '/api/customerservice',
  cart: '/api/cartservice',
  inventory: '/api/inventoryservice',
  order: '/api/orderservice',
  shipping: '/api/shoppingservice'
}

let productsCache = []

const routes = {
  '/': Home,
  '/cart': Cart,
  '/orders': Orders,
  '/inventory': Inventory,
  '/login': Login,
  '/signup': Signup
}

// Handle navigation
window.navigate = function (path) {
  window.history.pushState({}, path, window.location.origin + path)
  render()
}

window.onpopstate = render

async function render() {
  const path = window.location.pathname
  const component = routes[path] || Home
  app.innerHTML = await component()
}

function Header() {
  const path = window.location.pathname
  const user = state.user

  return `
  <header>
      <h1>ModernShop</h1>
      <nav>
        <a href="/" class="${path === '/' ? 'active' : ''}" onclick="event.preventDefault(); navigate('/')">Products</a>
        ${user ? `
          <a href="/cart" class="${path === '/cart' ? 'active' : ''}" onclick="event.preventDefault(); navigate('/cart')">Cart</a>
          <a href="/orders" class="${path === '/orders' ? 'active' : ''}" onclick="event.preventDefault(); navigate('/orders')">Orders</a>
          <a href="/inventory" class="${path === '/inventory' ? 'active' : ''}" onclick="event.preventDefault(); navigate('/inventory')">Inventory</a>
          <span style="margin-left: 1rem; color: var(--text-color); font-weight: 600;">${user.customerName}</span>
          <button class="btn" style="padding: 0.25rem 0.75rem; font-size: 0.875rem;" onclick="logout()">Logout</button>
        ` : `
          <a href="/login" class="${path === '/login' ? 'active' : ''}" onclick="event.preventDefault(); navigate('/login')">Login</a>
          <a href="/signup" class="${path === '/signup' ? 'active' : ''}" onclick="event.preventDefault(); navigate('/signup')">Sign Up</a>
        `}
      </nav>
    </header>
  `
}

async function Home() {
  let productsHtml = '<div class="loading">Loading products...</div>'

  try {
    // Attempt to fetch from the product service
    // Adjust the endpoint based on your actual Controller
    const response = await fetch('/api/productservice/products')
    if (!response.ok) throw new Error('Failed to fetch products')
    const products = await response.json()

    productsCache = products
    if (products.length === 0) {
      productsHtml = '<div class="loading">No products found.</div>'
    } else {
      productsHtml = `
        <div class="product-grid">
    ${products.map(product => `
              <div class="card">
                <img src="${product.imageUrl || `https://placehold.co/400x300?text=${encodeURIComponent(product.productName || 'Product')}`}" alt="${product.productName}" class="product-image">
                <div class="product-title">${product.productName || 'Unknown Product'}</div>
                <div class="product-price">$${product.productPrice || '0.00'}</div>
                <div style="color: var(--text-secondary); font-size: 0.875rem; margin-bottom: 1rem;">${product.productDescription || ''}</div>
                <button class="btn btn-primary" onclick="addToCart(${product.productId})">Add to Cart</button>
              </div>
            `).join('')
        }
          </div>
  `
    }
  } catch (e) {
    console.warn("Backend fetch failed, showing demo data:", e)
    productsHtml = `
      <div class="error">
        <strong>Backend Connection Failed</strong><br>
        Could not load products from <code>/api/productservice/products</code>.<br>
        Ensure the backend services (Zuul Gateway + Product Service) are running on port 8080.
      </div>
      
      <h2 style="margin: 2rem 0 1rem;">Demo Products (Visual Preview)</h2>
      <div class="product-grid">
         ${[1, 2, 3, 4, 5, 6].map(i => `
          <div class="card">
            <img src="https://placehold.co/400x300?text=Product+${i}" alt="Product ${i}" class="product-image">
            <div class="product-title">Premium Item ${i}</div>
            <div class="product-price">$${(i * 24.99).toFixed(2)}</div>
            <div style="color: var(--text-secondary); font-size: 0.875rem; margin-bottom: 1rem;">High quality premium item for your needs.</div>
            <button class="btn btn-primary" onclick="addToCart(${i})">Add to Cart</button>
          </div>
         `).join('')}
      </div>
    `
  }

  return `
    ${Header()}
    <main>
      ${productsHtml}
    </main>
  `
}

async function Login() {
  return `
    ${Header()}
    <main style="max-width: 400px; margin: 0 auto;">
      <div class="card">
        <h2 style="margin-bottom: 1.5rem;">Login</h2>
        <form onsubmit="handleLogin(event)">
          <div style="margin-bottom: 1rem;">
            <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Customer ID</label>
            <input type="number" name="customerId" placeholder="Enter your Customer ID" required>
            <small style="color: var(--text-secondary);">Use the ID you received upon signup.</small>
          </div>
          <button type="submit" class="btn btn-primary" style="width: 100%;">Login</button>
        </form>
        <p style="margin-top: 1rem; text-align: center;">
          Don't have an account? <a href="/signup" onclick="event.preventDefault(); navigate('/signup')" style="color: var(--primary-color);">Sign up</a>
        </p>
      </div>
    </main>
  `
}

async function Signup() {
  return `
    ${Header()}
    <main style="max-width: 500px; margin: 0 auto;">
      <div class="card">
        <h2 style="margin-bottom: 1.5rem;">Create Account</h2>
        <form onsubmit="handleSignup(event)">
          <div style="margin-bottom: 1rem;">
            <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Full Name</label>
            <input type="text" name="customerName" placeholder="John Doe" required>
          </div>
          <div style="margin-bottom: 1rem;">
            <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Email Address</label>
            <input type="email" name="customerEmail" placeholder="john@example.com" required>
          </div>
          <button type="submit" class="btn btn-primary" style="width: 100%;">Sign Up</button>
        </form>
        <p style="margin-top: 1rem; text-align: center;">
          Already have an account? <a href="/login" onclick="event.preventDefault(); navigate('/login')" style="color: var(--primary-color);">Login</a>
        </p>
      </div>
    </main>
  `
}

async function Cart() {
  if (!state.user) {
    navigate('/login')
    return ''
  }
  let cartHtml = '<div class="loading">Loading cart...</div>'
  try {
    const res = await fetch(`${API.cart}/api/cart/${state.user.customerId}`)
    if (res.ok) {
      const cart = await res.json()
      const items = (cart && cart.lineitem) || []
      if (items.length === 0) {
        cartHtml = '<p>Your cart is empty.</p>'
      } else {
        const total = items.reduce((s, i) => s + (i.quantity || 0) * (i.price || 0), 0)
        cartHtml = `
          ${items.map(i => `
            <div style="display:flex; justify-content:space-between; padding:0.5rem 0; border-bottom:1px solid var(--border-color, #eee);">
              <span>${i.productName || ('Product ' + i.productId)}</span>
              <span>x${i.quantity}</span>
              <span>$${((i.price || 0) * i.quantity).toFixed(2)}</span>
            </div>`).join('')}
          <div style="margin:1rem 0;"><strong>Total: $${total.toFixed(2)}</strong></div>
          <button class="btn btn-primary" onclick="placeOrder()">Place Order</button>
        `
      }
    } else {
      cartHtml = '<p>Your cart is empty.</p>'
    }
  } catch (e) {
    cartHtml = '<p>Could not load cart.</p>'
  }
  return `
    ${Header()}
    <main style="max-width: 700px; margin: 0 auto;">
      <h2>Your Cart</h2>
      ${cartHtml}
    </main>
  `
}

async function Orders() {
  if (!state.user) {
    navigate('/login')
    return ''
  }
  let ordersHtml = '<div class="loading">Loading orders...</div>'
  try {
    const res = await fetch(`${API.shipping}/customer/${state.user.customerId}/orders`)
    if (!res.ok) throw new Error('Failed to load orders')
    const data = await res.json()
    const orders = data.orders || []
    if (orders.length === 0) {
      ordersHtml = '<p>No orders yet.</p>'
    } else {
      ordersHtml = orders.map(o => {
        const items = (o.lineitem || []).map(i => `<li>${i.productName || ('Product ' + i.productId)} x${i.quantity}</li>`).join('')
        return `
          <div class="card" style="margin-top: 1rem; padding: 1rem;">
            <strong>Order #${o.orderid}</strong>
            <ul style="margin: 0.5rem 0;">${items || '<li>No items</li>'}</ul>
            <button class="btn" onclick="fetchOrderDetail(${o.orderid})">View details</button>
            <div id="order-detail-${o.orderid}"></div>
          </div>
        `
      }).join('')
    }
  } catch (e) {
    ordersHtml = '<p>Could not load orders.</p>'
  }
  return `
    ${Header()}
    <main style="max-width: 700px; margin: 0 auto;">
      <h2>Your Orders</h2>
      ${ordersHtml}
    </main>
  `
}

async function Inventory() {
  return `
    ${Header()}
    <main style="max-width: 500px; margin: 0 auto;">
      <h2>Inventory Lookup</h2>
      <p style="color: var(--text-secondary);">Enter an Inventory ID to check current stock.</p>
      <form onsubmit="checkInventory(event)">
        <div style="margin-bottom: 1rem;">
          <label style="display: block; margin-bottom: 0.5rem;">Inventory ID</label>
          <input type="number" name="inventoryId" placeholder="Enter Inventory ID" required>
        </div>
        <button type="submit" class="btn btn-primary">Check Stock</button>
      </form>
      <div id="inventory-result" style="margin-top: 1rem;"></div>
    </main>
  `
}

// Global actions
window.handleLogin = async (event) => {
  event.preventDefault()
  const formData = new FormData(event.target)
  const customerId = formData.get('customerId')

  try {
    const res = await fetch(`/api/customerservice/customer/searchCustomer/${customerId}`)
    if (!res.ok) throw new Error('Customer not found')
    const user = await res.json()

    state.user = user
    localStorage.setItem('user', JSON.stringify(user))
    alert(`Welcome back, ${user.customerName}!`)
    navigate('/')
  } catch (e) {
    alert('Login failed: ' + e.message)
  }
}

window.handleSignup = async (event) => {
  event.preventDefault()
  const formData = new FormData(event.target)
  const data = {
    customerName: formData.get('customerName'),
    customerEmail: formData.get('customerEmail'),
    // Default empty addresses for now as per entity structure
    customerBillingAddress: null,
    customerShippingAddress: null
  }

  try {
    const res = await fetch('/api/customerservice/customer/addCustomer', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    })

    if (!res.ok) throw new Error('Signup failed')
    const newUser = await res.json()

    alert(`Account created! Your Customer ID is ${newUser.customerId}. Please login with this ID.`)
    navigate('/login')
  } catch (e) {
    alert('Signup failed: ' + e.message)
  }
}

window.logout = () => {
  state.user = null
  localStorage.removeItem('user')
  navigate('/login')
}

window.addToCart = async (productId) => {
  if (!state.user) {
    alert('Please login to add items to cart')
    navigate('/login')
    return
  }
  const product = productsCache.find(p => p.productId === productId)
  const cartId = state.user.customerId
  let lineitem = []
  try {
    const res = await fetch(`${API.cart}/api/cart/${cartId}`)
    if (res.ok) {
      const cart = await res.json()
      lineitem = (cart && cart.lineitem) || []
    }
  } catch (e) { /* treat as a new cart */ }

  const existing = lineitem.find(i => i.productId === productId)
  if (existing) {
    existing.quantity += 1
  } else {
    lineitem.push({ productId, productName: product.productName, quantity: 1, price: Math.round(product.productPrice || 0) })
  }
  const payload = { cartid: cartId, lineitem }
  const headers = { 'Content-Type': 'application/json' }
  try {
    let res = await fetch(`${API.cart}/api/cart/${cartId}`, { method: 'PUT', headers, body: JSON.stringify(payload) })
    if (!res.ok) {
      // cart may not exist yet - create it
      res = await fetch(`${API.cart}/api/cart`, { method: 'POST', headers, body: JSON.stringify(payload) })
    }
    if (res.ok) {
      alert('Added to cart!')
    } else {
      alert('Failed to add to cart')
    }
  } catch (e) {
    alert('Failed to add to cart')
  }
}

window.placeOrder = async () => {
  try {
    const res = await fetch(`${API.shipping}/customer/${state.user.customerId}/order`, { method: 'POST' })
    if (!res.ok) throw new Error('Failed to place order')
    const order = await res.json()
    alert(`Order #${order.orderid} placed successfully!`)
    navigate('/orders')
  } catch (e) {
    alert('Order failed: ' + e.message)
  }
}

window.fetchOrderDetail = async (orderId) => {
  const el = document.querySelector(`#order-detail-${orderId}`)
  if (!el) return
  try {
    const res = await fetch(`${API.order}/api/order/${orderId}`)
    if (!res.ok) throw new Error('not found')
    const order = await res.json()
    const items = (order.lineitem || [])
      .map(i => `<li>${i.productName || ('Product ' + i.productId)} x${i.quantity} @ $${i.price}</li>`)
      .join('')
    el.innerHTML = `<ul>${items || '<li>No items</li>'}</ul>`
  } catch (e) {
    el.innerHTML = '<em>Order details unavailable.</em>'
  }
}

window.checkInventory = async (event) => {
  event.preventDefault()
  const id = new FormData(event.target).get('inventoryId')
  const el = document.querySelector('#inventory-result')
  if (!el) return
  el.innerHTML = 'Checking...'
  try {
    const res = await fetch(`${API.inventory}/api/inventory/${id}`)
    if (!res.ok) throw new Error('not found')
    const inv = await res.json()
    el.innerHTML = `<div class="card" style="padding: 1rem;">
      <strong>Inventory #${inv.inventoryId}</strong><br>
      Product ID: ${inv.productId}<br>
      Quantity in stock: ${inv.quantity}
    </div>`
  } catch (e) {
    el.innerHTML = '<em>No inventory record found for that ID.</em>'
  }
}

// Initial render
render()
