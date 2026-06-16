import './style.css'

const app = document.querySelector('#app')

const state = {
  user: JSON.parse(localStorage.getItem('user')) || null
}

const routes = {
  '/': Home,
  '/cart': Cart,
  '/orders': Orders,
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
  return `
    ${Header()}
    <main>
      <h2>Your Cart</h2>
      <div class="card" style="margin-top: 1rem;">
        <p>Cart functionality is under construction.</p>
        <button class="btn btn-primary" style="margin-top: 1rem;" onclick="navigate('/')">Continue Shopping</button>
      </div>
    </main>
  `
}

async function Orders() {
  if (!state.user) {
    navigate('/login')
    return ''
  }
  return `
    ${Header()}
    <main>
      <h2>Your Orders</h2>
      <div class="card" style="margin-top: 1rem;">
        <p>Order history is under construction.</p>
      </div>
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
  try {
    // Example POST to cart service
    // const res = await fetch('/api/cartservice/cart', { method: 'POST', body: JSON.stringify({ productId, quantity: 1 }) })
    console.log(`Adding product ${productId} to cart`)
    alert(`Product ${productId} added to cart! (Simulation)`)
  } catch (e) {
    alert('Failed to add to cart')
  }
}

// Initial render
render()
