import { useState } from 'react'
import { useAuth } from '../auth/AuthContext.jsx'

export default function LoginPage() {
  const { login } = useAuth()
  const [loginId, setLoginId] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault() // form 기본 동작(페이지 새로고침) 막기
    setError(null)
    setSubmitting(true)
    try {
      await login(loginId, password)
    } catch (err) {
      setError(err.message)
      setPassword('')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="login-wrap">
      <form className="login-card" onSubmit={handleSubmit}>
        <h1>민원 업무지원 시스템</h1>
        <p className="muted">직원 계정으로 로그인하세요.</p>

        <label>
          아이디
          <input value={loginId} onChange={(e) => setLoginId(e.target.value)} autoComplete="username" autoFocus />
        </label>
        <label>
          비밀번호
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
          />
        </label>

        {error && <p className="message error">{error}</p>}

        <button className="btn btn-primary" disabled={submitting || !loginId || !password}>
          {submitting ? '확인 중...' : '로그인'}
        </button>

        <div className="dev-accounts">
          <strong>개발용 가상 계정</strong> (비밀번호 모두 pass1234!)
          <br />관리자: admin · 직원: kimcs, leeyh, parkms, jungde, choihw
        </div>
      </form>
    </div>
  )
}
