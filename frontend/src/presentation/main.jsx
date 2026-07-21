/**
 * Punto de entrada del Frontend — monta la SPA React en el DOM.
 * Servido en producción por Nginx (frontend/nginx.conf → puerto 80).
 */
import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'
import './index.css'

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)
