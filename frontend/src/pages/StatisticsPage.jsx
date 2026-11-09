import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, formatDateTime } from '../api.js'
import DelayChart from '../components/DelayChart.jsx'
import StatusBadge from '../components/StatusBadge.jsx'

// "2026-10" → { from: "2026-10-01", to: "2026-11-01" }  (to는 포함하지 않음: 백엔드 규칙과 동일)
function monthToPeriod(month) {
  const [y, m] = month.split('-').map(Number)
  const next = m === 12 ? `${y + 1}-01` : `${y}-${String(m + 1).padStart(2, '0')}`
  return { from: `${month}-01`, to: `${next}-01` }
}

function lastMonth() {
  const d = new Date()
  d.setDate(1)
  d.setMonth(d.getMonth() - 1)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
}

export default function StatisticsPage() {
  const [month, setMonth] = useState(lastMonth()) // 기본값: 지난달
  const [byDepartment, setByDepartment] = useState([])
  const [byServiceType, setByServiceType] = useState([])
  const [overdue, setOverdue] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    if (!month) return
    let ignore = false
    setLoading(true)
    setError(null)

    const { from, to } = monthToPeriod(month)
    const query = `?from=${from}&to=${to}`

    // 서로 독립적인 통계 3개를 동시에 요청
    Promise.all([
      api(`/statistics/delays/by-department${query}`),
      api(`/statistics/delays/by-service-type${query}`),
      api('/statistics/overdue'),
    ])
      .then(([dept, type, over]) => {
        if (ignore) return
        setByDepartment(dept.items)
        setByServiceType(type.items)
        setOverdue(over)
      })
      .catch((e) => { if (!ignore) setError(e.message) })
      .finally(() => { if (!ignore) setLoading(false) })

    return () => { ignore = true }
  }, [month])

  // 요약 카드: 모든 민원은 업무 유형이 있으므로 업무 유형별 합계 = 기간 전체 합계
  const total = byServiceType.reduce((sum, i) => sum + i.totalCount, 0)
  const delayed = byServiceType.reduce((sum, i) => sum + i.delayedCount, 0)
  const rate = total ? Math.round((delayed * 1000) / total) / 10 : 0

  return (
    <section>
      <div className="page-title">
        <h2>통계 대시보드</h2>
        <label className="month-picker">
          기간
          <input type="month" value={month} onChange={(e) => setMonth(e.target.value)} />
        </label>
      </div>

      {error && <p className="message error">{error}</p>}
      {loading && <p className="message">불러오는 중...</p>}

      {!loading && !error && (
        <>
          <div className="stat-cards">
            <div className="stat-card">
              <span className="stat-label">접수 민원</span>
              <span className="stat-value">{total}<small>건</small></span>
            </div>
            <div className="stat-card">
              <span className="stat-label">지연 민원</span>
              <span className="stat-value text-danger">{delayed}<small>건</small></span>
            </div>
            <div className="stat-card">
              <span className="stat-label">지연율</span>
              <span className="stat-value">{rate}<small>%</small></span>
            </div>
            <div className="stat-card">
              <span className="stat-label">현재 기한 초과 (미완료)</span>
              <span className="stat-value text-danger">{overdue.length}<small>건</small></span>
            </div>
          </div>

          <div className="chart-grid">
            <DelayChart title="부서별 지연 현황" items={byDepartment} />
            <DelayChart title="업무 유형별 지연 현황" items={byServiceType} />
          </div>

          <div className="card">
            <h3>기한 초과 민원 <span className="muted">(기간과 관계없이 지금 기준)</span></h3>
            {overdue.length === 0 ? (
              <p className="muted">기한을 넘긴 미완료 민원이 없습니다.</p>
            ) : (
              <table className="table">
                <thead>
                  <tr>
                    <th>번호</th><th>제목</th><th>담당 부서</th><th>담당자</th><th>상태</th><th>처리기한</th>
                  </tr>
                </thead>
                <tbody>
                  {overdue.map((c) => (
                    <tr key={c.id}>
                      <td>{c.id}</td>
                      <td><Link to={`/complaints/${c.id}`}>{c.title}</Link></td>
                      <td>{c.departmentName ?? <span className="muted">미배정</span>}</td>
                      <td>{c.assigneeName ?? <span className="muted">미배정</span>}</td>
                      <td><StatusBadge status={c.status} label={c.statusLabel} /></td>
                      <td className="text-danger">{formatDateTime(c.dueAt)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </>
      )}
    </section>
  )
}
