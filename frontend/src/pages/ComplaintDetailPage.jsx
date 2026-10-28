import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api, formatDateTime } from '../api.js'
import StatusBadge from '../components/StatusBadge.jsx'
import StatusActionPanel from '../components/StatusActionPanel.jsx'

export default function ComplaintDetailPage() {
  const { id } = useParams() // 주소 /complaints/11 의 11

  const [complaint, setComplaint] = useState(null)
  const [histories, setHistories] = useState([])
  const [error, setError] = useState(null)
  const [reloadKey, setReloadKey] = useState(0) // 이 값이 바뀌면 상세·이력을 다시 불러온다

  useEffect(() => {
    let ignore = false
    setError(null)

    // 상세와 이력은 서로 독립적이라 동시에 요청한다
    Promise.all([api(`/complaints/${id}`), api(`/complaints/${id}/histories`)])
      .then(([c, h]) => {
        if (ignore) return
        setComplaint(c)
        setHistories(h)
      })
      .catch((e) => { if (!ignore) setError(e.message) })

    return () => { ignore = true }
  }, [id, reloadKey])

  if (error) {
    return (
      <section>
        <p className="message error">{error}</p>
        <Link to="/complaints">← 목록으로</Link>
      </section>
    )
  }
  if (!complaint) return <p className="message">불러오는 중...</p>

  return (
    <section>
      <Link to="/complaints" className="back">← 목록으로</Link>

      <div className="page-title">
        <h2>
          #{complaint.id} {complaint.title}
        </h2>
        <StatusBadge status={complaint.status} label={complaint.statusLabel} />
        {complaint.delayed && <span className="badge badge-delayed">지연</span>}
      </div>

      <div className="card">
        <dl className="info-grid">
          <dt>신청인</dt><dd>{complaint.applicantName}</dd>
          <dt>업무 유형</dt><dd>{complaint.serviceTypeName}</dd>
          <dt>담당 부서</dt><dd>{complaint.departmentName ?? '미배정'}</dd>
          <dt>담당자</dt><dd>{complaint.assigneeName ?? '미배정'}</dd>
          <dt>접수일시</dt><dd>{formatDateTime(complaint.receivedAt)}</dd>
          <dt>처리기한</dt>
          <dd className={complaint.delayed ? 'text-danger' : ''}>{formatDateTime(complaint.dueAt)}</dd>
          <dt>처리완료</dt><dd>{formatDateTime(complaint.completedAt)}</dd>
        </dl>
        <h3>민원 내용</h3>
        <p className="content">{complaint.content}</p>
      </div>

      <StatusActionPanel complaint={complaint} onChanged={() => setReloadKey((k) => k + 1)} />

      <div className="card">
        <h3>처리 이력</h3>
        <ol className="timeline">
          {histories.map((h) => (
            <li key={h.id}>
              <span className="time">{formatDateTime(h.changedAt)}</span>
              <span className={`badge badge-${h.toStatus}`}>{h.toStatusLabel}</span>
              <span className="by">{h.changedByName ?? '시스템'}</span>
              {h.memo && <span className="memo">{h.memo}</span>}
            </li>
          ))}
        </ol>
      </div>
    </section>
  )
}
