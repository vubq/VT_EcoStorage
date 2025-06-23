import { request } from '../http'

export const ExportOrderService = {
  getListExportOrder(params: DataTable.Request) {
    return request.Get<Service.ResponseResult<any>>('/api/export-order/list', { params })
  },
  getReferenceData() {
    return request.Get<Service.ResponseResult<any>>('/api/export-order/reference-data')
  },
  createOrUpdateExportOrder(data: any) {
    return request.Post<Service.ResponseResult<any>>('/api/export-order/create-or-update', data)
  },
  getExportOrder(exportOrderId: string) {
    return request.Get<Service.ResponseResult<any>>(`/api/export-order/${exportOrderId}`)
  },
}
