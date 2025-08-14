import React, { useEffect, useState } from 'react'
import { createRoot } from 'react-dom/client'

function App() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [token, setToken] = useState('')
  const [products, setProducts] = useState([])
  const [cart, setCart] = useState([])

  async function login() {
    const res = await fetch('http://localhost:8081/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    })
    const data = await res.json()
    if (data.token) setToken(data.token)
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
      body: JSON.stringify({ id: product.id, name: product.name, qty: 1 })
    })
    const data = await res.json()
    setCart(data)
  }

  async function loadCart() {
    const res = await fetch(`http://localhost:8083/api/cart/${username}`)
    const data = await res.json()
    setCart(data)
  }

  useEffect(() => { loadProducts() }, [])

  return (
    <div style={{ fontFamily: 'system-ui', padding: 24 }}>
      <h1>E-commerce</h1>
      <div style={{ display: 'flex', gap: 32 }}>
        <div>
          <h2>Login</h2>
          <input placeholder="username" value={username} onChange={e => setUsername(e.target.value)} />
          <input placeholder="password" type="password" value={password} onChange={e => setPassword(e.target.value)} />
          <button onClick={login}>Login</button>
          {token && <div>Logged in: {username}</div>}
        </div>
        <div>
          <h2>Products</h2>
          <ul>
            {products.map(p => (
              <li key={p.id}>
                {p.name} - ${p.price}
                <button style={{ marginLeft: 8 }} onClick={() => addToCart(p)}>Add</button>
              </li>
            ))}
          </ul>
        </div>
        <div>
          <h2>Cart ({username || 'anonymous'})</h2>
          <button onClick={loadCart}>Refresh</button>
          <ul>
            {cart.map((c, idx) => (
              <li key={idx}>{c.name} x{c.qty}</li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  )
}

createRoot(document.getElementById('root')).render(<App />)


