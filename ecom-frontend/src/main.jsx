import React, { useEffect, useState } from 'react'
import { createRoot } from 'react-dom/client'

function App() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [showRegister, setShowRegister] = useState(false)
  const [regName, setRegName] = useState('')
  const [regEmail, setRegEmail] = useState('')
  const [regAddress, setRegAddress] = useState('')
  const [regPassword, setRegPassword] = useState('')
  const [token, setToken] = useState('')
  const [flash, setFlash] = useState('')
  const [products, setProducts] = useState([])
  const [cart, setCart] = useState([])
  const [view, setView] = useState('login') // 'login' | 'products' | 'cart'
  const [highlightRegister, setHighlightRegister] = useState(false)

  async function login() {
    setFlash('')
    try {
      const identifier = (username || '').trim()
      const pwd = (password || '').trim()
      const res = await fetch('http://localhost:8081/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: identifier, password: pwd })
      })
      let data
      try {
        data = await res.json()
      } catch (e) {
        data = null
      }
      const expectedPrefix = `dummy-token-${identifier}`.toLowerCase()
      if (
        res.ok && data && data.token && data.username &&
        String(data.username).toLowerCase() === identifier.toLowerCase() &&
        String(data.token).toLowerCase().startsWith(expectedPrefix)
      ) {
        setToken(data.token)
        setView('products')
        setHighlightRegister(false)
        return
      }
      if (res.status === 404) {
        if (data) {
          console.log('Login failed (404):', data)
        }
        setFlash('You are not registered customer. Please register first.')
        setShowRegister(false)
        setHighlightRegister(true)
        setToken('')
        setView('login')
        return
      }
      if (data) {
        console.log('Login failed:', res.status, data)
      }
      setFlash('Invalid username or password. If you are not registered, please register first.')
      setShowRegister(false)
      setHighlightRegister(true)
      setToken('')
      setView('login')
    } catch (err) {
      console.log('Login error:', err)
      setFlash('Invalid username or password. If you are not registered, please register first.')
      setShowRegister(false)
      setHighlightRegister(true)
      setToken('')
      setView('login')
    }
  }

  async function register() {
    setFlash('')
    const res = await fetch('http://localhost:8081/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name: regName, email: regEmail, address: regAddress, password: regPassword })
    })
    const data = await res.json()
    if (res.ok && data && !data.error) {
      setFlash('Registration successful. Please login.')
      // Reset registration fields and switch to login view
      setRegName(''); setRegEmail(''); setRegAddress(''); setRegPassword('')
      setShowRegister(false)
      setView('login')
      return
    }
    setFlash(data?.error || 'Registration failed')
  }

  async function loadProducts() {
    const res = await fetch('http://localhost:8082/api/products')
    const data = await res.json()
    setProducts(data)
  }

  async function addToCart(product) {
    const res = await fetch(`http://localhost:8083/api/cart/${username}/items`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ productId: product.id, name: product.name, qty: 1 })
    })
    const data = await res.json()
    setCart(data)
    // Ensure state matches server
    await loadCart()
  }

  async function incrementItem(productId) {
    const res = await fetch(`http://localhost:8083/api/cart/${username}/items/${productId}/increment`, {
      method: 'POST'
    })
    const data = await res.json()
    setCart(data)
    await loadCart()
  }

  async function decrementItem(productId) {
    const res = await fetch(`http://localhost:8083/api/cart/${username}/items/${productId}/decrement`, {
      method: 'POST'
    })
    const data = await res.json()
    setCart(data)
    await loadCart()
  }

  async function deleteItem(productId) {
    const res = await fetch(`http://localhost:8083/api/cart/${username}/items/${productId}`, {
      method: 'DELETE'
    })
    const data = await res.json()
    setCart(data)
    await loadCart()
  }

  async function clearCart() {
    const res = await fetch(`http://localhost:8083/api/cart/${username}`, {
      method: 'DELETE'
    })
    const data = await res.json()
    setCart(data)
    await loadCart()
  }

  async function loadCart() {
    const res = await fetch(`http://localhost:8083/api/cart/${username}`)
    const data = await res.json()
    setCart(data)
  }

  // Load products + cart when on products view after login (to show qty badges)
  useEffect(() => {
    if (token && view === 'products') {
      loadProducts()
      if (username) {
        loadCart()
      }
    }
  }, [token, view])

  // Load cart when on cart view after login
  useEffect(() => {
    if (token && view === 'cart' && username) {
      loadCart()
    }
  }, [token, view, username])

  // If not logged in, show login view
  if (!token || view === 'login') {
    return (
      <div style={{ fontFamily: 'system-ui', padding: 24, maxWidth: 360 }}>
        <h1>PowerRack - one stop for all gym gears</h1>
        {flash && (
          <div style={{
            backgroundColor: '#eef6ff', border: '1px solid #bfdbfe', color: '#1e40af',
            padding: '8px 12px', borderRadius: 6, marginBottom: 12
          }}>{flash}</div>
        )}
        <div style={{ display: 'flex', gap: 12, marginBottom: 12 }}>
          <button onClick={() => { setShowRegister(false); setHighlightRegister(false); }} disabled={!showRegister}>Login</button>
          <button
            onClick={() => { setShowRegister(true); setHighlightRegister(false); }}
            disabled={showRegister}
            style={highlightRegister ? { backgroundColor: '#2563eb', color: '#fff', border: '1px solid #1d4ed8', boxShadow: '0 0 0 3px rgba(59,130,246,0.3)' } : undefined}
          >
            Register
          </button>
        </div>
        {!showRegister && (
          <>
            <h2>Login</h2>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
              <input placeholder="email or username" value={username} onChange={e => setUsername(e.target.value)} />
              <input placeholder="password" type="password" value={password} onChange={e => setPassword(e.target.value)} />
              <button onClick={login}>Login</button>
            </div>
          </>
        )}
        {showRegister && (
          <>
            <h2>Register</h2>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
              <input placeholder="name" value={regName} onChange={e => setRegName(e.target.value)} />
              <input placeholder="email" type="email" value={regEmail} onChange={e => setRegEmail(e.target.value)} />
              <input placeholder="address" value={regAddress} onChange={e => setRegAddress(e.target.value)} />
              <input placeholder="create password" type="password" value={regPassword} onChange={e => setRegPassword(e.target.value)} />
              <button onClick={register}>Create account</button>
            </div>
          </>
        )}
      </div>
    )
  }

  // Products page
  if (view === 'products') {
    return (
      <div style={{ fontFamily: 'system-ui', padding: 24 }}>
        <h1>PowerRack - one stop for all gym gears</h1>
        <div style={{ marginBottom: 16 }}>Logged in as <strong>{username}</strong></div>
        <div>
          <h2>Products</h2>
          <ul>
            {products.map(p => (
              <li key={p.id}>
                {p.name} - ₹{Number(p.price).toLocaleString('en-IN')}
                {(() => {
                  const found = cart.find(c => String(c.productId) === String(p.id))
                  const qty = found ? Number(found.qty || 0) : 0
                  if (!qty) {
                    return (
                      <button style={{ marginLeft: 8 }} onClick={() => addToCart(p)}>Add to cart</button>
                    )
                  }
                  return (
                    <span style={{ marginLeft: 8, display: 'inline-flex', gap: 8, alignItems: 'center' }}>
                      <button onClick={() => decrementItem(p.id)}>-</button>
                      <span>{qty}</span>
                      <button onClick={() => incrementItem(p.id)}>+</button>
                    </span>
                  )
                })()}
              </li>
            ))}
          </ul>
          <div style={{ marginTop: 16 }}>
            <button onClick={() => setView('cart')}>Go to cart</button>
          </div>
        </div>
      </div>
    )
  }

  // Cart page
  return (
    <div style={{ fontFamily: 'system-ui', padding: 24 }}>
      <h1>Your Cart</h1>
      <div style={{ marginBottom: 16 }}>User: <strong>{username}</strong></div>
      <button onClick={() => setView('products')}>Back to products</button>
      <div style={{ marginTop: 16 }}>
        <ul>
          {cart.length === 0 && <li>No items yet.</li>}
          {cart.map((c, idx) => (
            <li key={idx}>
              {c.name}
              <span style={{ marginLeft: 8, display: 'inline-flex', gap: 8, alignItems: 'center' }}>
                <button onClick={() => decrementItem(c.productId || c.id)}>-</button>
                <span>{Number(c.qty || 0)}</span>
                <button onClick={() => incrementItem(c.productId || c.id)}>+</button>
                <button
                  onClick={() => deleteItem(c.productId || c.id)}
                  style={{
                    marginLeft: 8,
                    backgroundColor: '#e02424',
                    color: '#fff',
                    border: 'none',
                    borderRadius: 4,
                    padding: '2px 8px',
                    cursor: 'pointer'
                  }}
                  aria-label="Delete item"
                  title="Delete"
                >
                  🗑️
                </button>
              </span>
            </li>
          ))}
        </ul>
        <div style={{ marginTop: 16 }}>
          <button onClick={clearCart} disabled={cart.length === 0}>Clear cart</button>
        </div>
      </div>
    </div>
  )
}

createRoot(document.getElementById('root')).render(<App />)


