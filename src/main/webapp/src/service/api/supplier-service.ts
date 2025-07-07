import { request } from '../http'

export const SupplierService = {
  getSuppliers(params: DataTable.Request) {
    return request.Get<Service.ResponseResult<any>>('/api/supplier/list', { params })
  },
  getSupplier(id: string) {
    return request.Get<Service.ResponseResult<any>>(`/api/supplier/${id}`)
  },
  createOrUpdateSupplier(data: any) {
    return request.Post<Service.ResponseResult<any>>('/api/supplier/create-or-update', data)
  },
  getPurchaseOrders(params: DataTable.Request, supplierId: string) {
    return request.Get<Service.ResponseResult<any>>(`/api/supplier/purchase-orders/${supplierId}`, { params })
  },
}
