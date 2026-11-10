import { Navigate, NavLink, Route, Routes } from 'react-router-dom'
import { useAuth } from './auth/AuthContext.jsx'
import LoginPage from './pages/LoginPage.jsx'
import ComplaintListPage from './pages/ComplaintListPage.jsx'
import ComplaintDetailPage from './pages/ComplaintDetailPage.jsx'
import StatisticsPage from './pages/StatisticsPage.jsx'

export default function App() {
  const { user, checking, logout } = useAuth()

  if (checking) return <p className="message" style={{ padding: '2rem' }}>확인 중...</p>
  if (!user) return <LoginPage />   // 로그인 전에는 어떤 화면도 보여주지 않는다

  return (
    <div className="layout">
      <header className="header">
        <h1 className="logo">민원 업무지원 시스템</h1>
        <nav className="nav">
          <NavLink to="/complaints">민원 목록</NavLink>
          <NavLink to="/statistics">통계</NavLink>
        </nav>
        <div className="user-box">
          <span>
            {user.name} <span className="role">{user.roleLabel} · {user.departmentName}</span>
          </span>
          <button className="btn-logout" onClick={logout}>로그아웃</button>
        </div>
      </header>

      <main className="main">
        {/* 주소(URL)에 따라 어떤 화면을 보여줄지 정한다 */}
        <Routes>
          <Route path="/" element={<Navigate to="/complaints" replace />} />
          <Route path="/complaints" element={<ComplaintListPage />} />
          <Route path="/complaints/:id" element={<ComplaintDetailPage />} />
          <Route path="/statistics" element={<StatisticsPage />} />
          <Route path="*" element={<p className="message">없는 화면입니다.</p>} />
        </Routes>
      </main>
    </div>
  )
}
