import { fetchUpdateToken } from '@/service'
import { useAuthStore } from '@/store'
import { local } from '@/utils'
import {
  ERROR_NO_TIP_STATUS,
  ERROR_STATUS,
} from './config'

type ErrorStatus = keyof typeof ERROR_STATUS

export function handleResponseError(response: Response) {
  const error: Service.RequestError = {
    errorType: 'Response Error',
    code: 0,
    message: ERROR_STATUS.default,
    data: null,
  }
  const errorCode: ErrorStatus = response.status as ErrorStatus
  const message = ERROR_STATUS[errorCode] || ERROR_STATUS.default
  Object.assign(error, { code: errorCode, message })

  showError(error)

  return error
}

export function handleBusinessError(data: Record<string, any>, config: Required<Service.BackendConfig>) {
  const { codeKey, msgKey } = config
  const error: Service.RequestError = {
    errorType: 'Business Error',
    code: data[codeKey],
    message: data[msgKey],
    data: data.data,
  }

  showError(error)

  return error
}

export function handleServiceResult(data: any, isSuccess: boolean = true) {
  const result = {
    isSuccess,
    errorType: null,
    ...data,
  }
  return result
}

export async function handleRefreshToken() {
  const authStore = useAuthStore()
  const isAutoRefresh = import.meta.env.VITE_AUTO_REFRESH_TOKEN === 'Y'
  if (!isAutoRefresh) {
    await authStore.logout()
    return
  }

  const { data } = await fetchUpdateToken({ refreshToken: local.get('refreshToken') })
  if (data) {
    local.set('accessToken', data.accessToken)
    local.set('refreshToken', data.refreshToken)
  }
  else {
    await authStore.logout()
  }
}

export function showError(error: Service.RequestError) {
  const code = Number(error.code)
  if (ERROR_NO_TIP_STATUS.includes(code))
    return

  window.$message.error(error.message)
}
