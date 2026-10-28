import { Navigate, NavLink, Route, Routes } from 'react-router-dom'
import ComplaintListPage from './pages/ComplaintListPage.jsx'
import ComplaintDetailPage from './pages/ComplaintDetailPage.jsx'

export default function App() {
  return (
    <div className="layout">
      <header className="header">
        <h1 className="logo">민원 업무지원 시스템</h1>
        <nav className="nav">
          <NavLink to="/complaints">민원 목록</NavLink>
        </nav>
      </header>

      <main className="main">
        {/* 주소(URL)에 따라 어떤 화면을 보여줄지 정한다 */}
        <Routes>
          <Route path="/" element={<Navigate to="/complaints" replace />} />
          <Route path="/complaints" element={<ComplaintListPage />} />
          <Route path="/complaints/:id" element={<ComplaintDetailPage />} />
          <Route path="*" element={<p className="message">없는 화면입니다.</p>} />
        </Routes>
      </main>
    </div>
  )
}
