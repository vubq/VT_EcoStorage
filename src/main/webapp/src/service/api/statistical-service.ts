import { request } from '../http'

export const StatisticalService = {
  getStatistical(startDate: string, endDate: string, warehouseId: string, keyword: string, onlyWithTransaction: boolean) {
    return request.Get<Service.ResponseResult<any>>('/api/statistical?' + 'startDate=' + startDate + '&endDate=' + endDate + '&warehouseId=' + warehouseId + '&keyword=' + keyword + '&onlyWithTransaction=' + onlyWithTransaction)
  },
  getReferenceData() {
    return request.Get<Service.ResponseResult<any>>('/api/statistical/reference-data')
  },
}
