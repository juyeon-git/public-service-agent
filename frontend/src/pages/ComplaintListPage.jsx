import { useEffect, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { api, formatDateTime } from '../api.js'
import { STATUSES } from '../constants.js'
import StatusBadge from '../components/StatusBadge.jsx'

export default function ComplaintListPage() {
  // 상태 필터는 주소(?status=RECEIVED)에 둔다 → 새로고침·뒤로가기해도 필터가 유지된다
  const [searchParams, setSearchParams] = useSearchParams()
  const status = searchParams.get('status') ?? ''
  const navigate = useNavigate()

  // state: 값이 바뀌면 화면이 다시 그려진다
  const [complaints, setComplaints] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  // effect: status가 바뀔 때마다 API를 다시 호출한다
  useEffect(() => {
    let ignore = false // 응답이 늦게 도착해 이전 필터 결과로 덮어쓰는 것을 막는다
    setLoading(true)
    setError(null)

    api(`/complaints${status ? `?status=${status}` : ''}`)
      .then((data) => { if (!ignore) setComplaints(data) })
      .catch((e) => { if (!ignore) setError(e.message) })
      .finally(() => { if (!ignore) setLoading(false) })

    return () => { ignore = true }
  }, [status])

  const changeFilter = (code) => setSearchParams(code ? { status: code } : {})
  const delayedCount = complaints.filter((c) => c.delayed).length

  return (
    <section>
      <div className="page-title">
        <h2>민원 목록</h2>
        <span className="summary">
          총 {complaints.length}건 · 지연 <strong className="text-danger">{delayedCount}</strong>건
        </span>
      </div>

      <div className="filters">
        <button className={status === '' ? 'active' : ''} onClick={() => changeFilter('')}>전체</button>
        {STATUSES.map((s) => (
          <button key={s.code} className={status === s.code ? 'active' : ''} onClick={() => changeFilter(s.code)}>
            {s.label}
          </button>
        ))}
      </div>

      {error && <p className="message error">{error}</p>}
      {loading && <p className="message">불러오는 중...</p>}

      {!loading && !error && (
        <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                <th>번호</th>
                <th>제목</th>
                <th>업무 유형</th>
                <th>신청인</th>
                <th>담당 부서</th>
                <th>담당자</th>
                <th>상태</th>
                <th>접수일시</th>
                <th>처리기한</th>
              </tr>
            </thead>
            <tbody>
              {complaints.length === 0 && (
                <tr><td colSpan={9} className="empty">조건에 맞는 민원이 없습니다.</td></tr>
              )}
              {complaints.map((c) => (
                <tr key={c.id} className="clickable" onClick={() => navigate(`/complaints/${c.id}`)}>
                  <td>{c.id}</td>
                  <td className="title-cell">
                    {c.title}
                    {c.delayed && <span className="badge badge-delayed">지연</span>}
                  </td>
                  <td>{c.serviceTypeName}</td>
                  <td>{c.applicantName}</td>
                  <td>{c.departmentName ?? <span className="muted">미배정</span>}</td>
                  <td>{c.assigneeName ?? <span className="muted">미배정</span>}</td>
                  <td><StatusBadge status={c.status} label={c.statusLabel} /></td>
                  <td>{formatDateTime(c.receivedAt)}</td>
                  <td className={c.delayed ? 'text-danger' : ''}>{formatDateTime(c.dueAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  )
}
