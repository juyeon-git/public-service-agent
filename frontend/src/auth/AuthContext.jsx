import { createContext, useContext, useEffect, useState } from 'react'
import { api, setUnauthorizedHandler } from '../api.js'

// Context: 로그인 사용자 정보를 모든 화면이 props 전달 없이 꺼내 쓸 수 있게 하는 "공용 보관함"
const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)        // 로그인한 사용자 (없으면 null)
  const [checking, setChecking] = useState(true) // 처음 접속 시 "이미 로그인돼 있나" 확인 중

  useEffect(() => {
    setUnauthorizedHandler(() => setUser(null))   // 세션 만료 → 로그인 화면

    // 새로고침해도 세션(쿠키)이 살아 있으면 로그인 상태 유지
    api('/auth/me')
      .then(setUser)
      .catch(() => setUser(null))
      .finally(() => setChecking(false))
  }, [])

  const login = async (loginId, password) => {
    // 로그아웃하면 서버가 CSRF 토큰 쿠키도 지운다 → 토큰이 없으면 GET 요청으로 새 토큰부터 받는다
    if (!document.cookie.includes('XSRF-TOKEN=')) {
      await api('/auth/me').catch(() => {})
    }
    const me = await api('/auth/login', { method: 'POST', body: { loginId, password } })
    setUser(me)
  }

  const logout = async () => {
    try {
      await api('/auth/logout', { method: 'POST' })
    } finally {
      setUser(null)
    }
  }

  return (
    <AuthContext.Provider value={{ user, checking, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
