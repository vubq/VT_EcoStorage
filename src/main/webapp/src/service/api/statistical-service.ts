import { request } from '../http'

export const StatisticalService = {
  getStatistical(startDate: string, endDate: string) {
    return request.Get<Service.ResponseResult<any>>('/api/statistical?' + 'startDate=' + startDate + '&endDate=' + endDate)
  },
}
