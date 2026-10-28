import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    // 화면(5173)에서 /api 로 시작하는 요청을 Spring Boot(8080)로 대신 전달한다.
    // 브라우저 입장에서는 같은 주소로 보내는 것이라 CORS 설정이 필요 없다.
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
