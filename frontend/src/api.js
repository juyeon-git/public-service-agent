// 백엔드 API 호출을 한 곳에서 처리한다.
// 백엔드의 ErrorResponse(code, message)를 그대로 화면에 보여줄 수 있도록 ApiError로 바꿔 던진다.

export class ApiError extends Error {
  constructor(status, code, message) {
    super(message)
    this.status = status
    this.code = code
  }
}

export async function api(path, { method = 'GET', body } = {}) {
  let res
  try {
    res = await fetch(`/api${path}`, {
      method,
      headers: body ? { 'Content-Type': 'application/json' } : undefined,
      body: body ? JSON.stringify(body) : undefined,
    })
  } catch {
    throw new ApiError(0, 'NETWORK_ERROR', '서버에 연결할 수 없습니다.')
  }

  const text = await res.text()
  let data = null
  try {
    data = text ? JSON.parse(text) : null
  } catch {
    data = null
  }

  if (!res.ok) {
    if (res.status >= 500 && !data) {
      throw new ApiError(res.status, 'SERVER_ERROR', '서버가 응답하지 않습니다. Spring Boot가 실행 중인지 확인하세요.')
    }
    throw new ApiError(res.status, data?.code ?? 'UNKNOWN', data?.message ?? '요청을 처리하지 못했습니다.')
  }
  return data
}

// "2026-10-28T14:20:00" → "2026-10-28 14:20"
export function formatDateTime(value) {
  return value ? value.replace('T', ' ').slice(0, 16) : '-'
}
