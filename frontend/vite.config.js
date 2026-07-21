import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  base: '/',
  build: {
    // Evita crossorigin en script/link que exige CORS en Nginx sin cabeceras
    modulePreload: false,
  },
})
