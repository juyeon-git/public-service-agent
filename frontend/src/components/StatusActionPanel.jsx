import { useEffect, useState } from 'react'
import { api } from '../api.js'
import { useAuth } from '../auth/AuthContext.jsx'

// 현재 상태에서 누를 수 있는 버튼 (백엔드 Complaint 엔티티의 상태 전이 규칙과 같게 맞춤)
// ※ 화면에서 버튼을 숨기는 것은 "편의"일 뿐이고, 최종 검사는 항상 백엔드가 한다.
const ACTIONS_BY_STATUS = {
  RECEIVED: ['ASSIGNED', 'REJECTED'],
  ASSIGNED: ['ASSIGNED', 'IN_PROGRESS', 'REJECTED'],
  IN_PROGRESS: ['COMPLETED', 'REJECTED'],
  COMPLETED: [],
  REJECTED: [],
}

export default function StatusActionPanel({ complaint, onChanged }) {
  const { user } = useAuth()
  const isAdmin = user.role === 'ADMIN'
  const isMine = complaint.assigneeId === user.employeeId

  const [employees, setEmployees] = useState([])
  const [assigneeId, setAssigneeId] = useState('')  // 배정할 담당자
  const [memo, setMemo] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState(null)
  const [message, setMessage] = useState(null)

  // 담당자 목록은 배정 권한이 있는 관리자만 불러온다
  useEffect(() => {
    if (!isAdmin) return
    api('/employees')
      .then(setEmployees)
      .catch((e) => setError(e.message))
  }, [isAdmin])

  // 권한 규칙 (백엔드 ComplaintService.checkPermission과 같게)
  //  - 배정: 관리자만 / 처리 시작·완료·반려: 관리자 또는 담당자 본인
  const canDo = (toStatus) => (toStatus === 'ASSIGNED' ? isAdmin : isAdmin || isMine)
  const statusActions = ACTIONS_BY_STATUS[complaint.status] ?? []
  const actions = statusActions.filter(canDo)

  const labelOf = (toStatus) => ({
    ASSIGNED: complaint.status === 'ASSIGNED' ? '재배정' : '배정',
    IN_PROGRESS: '처리 시작',
    COMPLETED: '처리 완료',
    REJECTED: '반려',
  })[toStatus]

  const submit = async (toStatus) => {
    setError(null)
    setMessage(null)

    // 화면에서 먼저 확인 (빠른 안내용). 백엔드도 같은 검사를 다시 한다.
    if (toStatus === 'ASSIGNED' && !assigneeId) {
      setError('배정할 담당자를 선택하세요.')
      return
    }
    if (toStatus === 'REJECTED' && !memo.trim()) {
      setError('반려 사유를 메모에 입력하세요.')
      return
    }

    setSubmitting(true) // 처리 중에는 버튼을 막아 두 번 클릭(중복 요청)을 방지
    try {
      // 작업자(누가 바꿨는지)는 보내지 않는다 → 서버가 로그인 세션에서 꺼낸다
      await api(`/complaints/${complaint.id}/status`, {
        method: 'PATCH',
        body: {
          toStatus,
          assigneeId: toStatus === 'ASSIGNED' ? Number(assigneeId) : null,
          memo: memo.trim() || null,
        },
      })
      setMemo('')
      setAssigneeId('')
      setMessage(`'${labelOf(toStatus)}' 처리되었습니다.`)
      onChanged() // 부모(상세 화면)에게 알려서 상세·이력을 다시 불러오게 한다
    } catch (e) {
      setError(`[${e.code}] ${e.message}`) // 백엔드 ErrorResponse를 그대로 보여준다
    } finally {
      setSubmitting(false)
    }
  }

  let notice = null
  if (statusActions.length === 0) notice = '처리가 끝난 민원이라 상태를 바꿀 수 없습니다.'
  else if (actions.length === 0) notice = '본인에게 배정된 민원만 처리할 수 있습니다. (배정은 관리자만 가능)'

  return (
    <div className="card">
      <h3>상태 변경</h3>

      {notice ? (
        <p className="muted">{notice}</p>
      ) : (
        <>
          {actions.includes('ASSIGNED') && (
            <div className="form-row">
              <label>
                담당자
                <select value={assigneeId} onChange={(e) => setAssigneeId(e.target.value)}>
                  <option value="">선택하세요</option>
                  {employees.map((emp) => (
                    <option key={emp.id} value={emp.id}>{emp.name} · {emp.departmentName}</option>
                  ))}
                </select>
              </label>
            </div>
          )}

          <div className="form-row">
            <label>
              메모 <span className="muted">(반려 시 필수)</span>
              <input
                type="text"
                value={memo}
                maxLength={500}
                placeholder="처리 내용이나 반려 사유"
                onChange={(e) => setMemo(e.target.value)}
              />
            </label>
          </div>

          <div className="actions">
            {actions.map((toStatus) => (
              <button
                key={toStatus}
                className={`btn ${toStatus === 'REJECTED' ? 'btn-danger' : 'btn-primary'}`}
                disabled={submitting}
                onClick={() => submit(toStatus)}
              >
                {labelOf(toStatus)}
              </button>
            ))}
          </div>
        </>
      )}

      {error && <p className="message error">{error}</p>}
      {message && <p className="message success">{message}</p>}
    </div>
  )
}
