// 상태 코드에 따라 색이 다른 작은 표시. props로 status와 label을 받는다.
export default function StatusBadge({ status, label }) {
  return <span className={`badge badge-${status}`}>{label}</span>
}
