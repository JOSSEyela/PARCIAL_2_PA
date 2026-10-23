import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// El proxy evita problemas de CORS: /api y /ws se reenvían al backend Spring (8080).
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8080',
      '/ws': { target: 'ws://localhost:8080', ws: true },
    },
  },
});
