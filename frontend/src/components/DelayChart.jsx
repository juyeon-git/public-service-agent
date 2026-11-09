// 지연 통계 막대그래프 (라이브러리 없이 CSS 막대로 그림)
// 막대 전체 길이 = 전체 건수, 빨간 부분 = 지연 건수
export default function DelayChart({ title, items }) {
  const maxTotal = Math.max(1, ...items.map((i) => i.totalCount))

  return (
    <div className="card">
      <h3>{title}</h3>
      {items.length === 0 && <p className="muted">이 기간에 접수된 민원이 없습니다.</p>}

      <ul className="bar-chart">
        {items.map((item) => (
          <li key={item.name ?? 'none'}>
            <span className="bar-label">{item.name ?? '미배정'}</span>

            <div className="bar-track">
              <div className="bar" style={{ width: `${(item.totalCount / maxTotal) * 100}%` }}>
                <div
                  className="bar-delayed"
                  style={{ width: `${item.totalCount ? (item.delayedCount / item.totalCount) * 100 : 0}%` }}
                />
              </div>
            </div>

            <span className="bar-value">
              지연 <strong className={item.delayedCount ? 'text-danger' : ''}>{item.delayedCount}</strong>
              {' / '}전체 {item.totalCount}건 · {item.delayRate}%
              <span className="muted">
                {' · '}평균 처리 {item.avgProcessingDays == null ? '-' : `${item.avgProcessingDays}일`}
              </span>
            </span>
          </li>
        ))}
      </ul>

      <div className="legend">
        <span><i className="dot dot-delayed" />지연</span>
        <span><i className="dot dot-ok" />기한 내</span>
      </div>
    </div>
  )
}
