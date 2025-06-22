import { request } from '../http'

export const PurchaseOrderService = {
  getListPurchaseOrder(params: DataTable.Request) {
    return request.Get<Service.ResponseResult<any>>('/api/purchase-order/list', { params })
  },
  getReferenceData() {
    return request.Get<Service.ResponseResult<any>>('/api/purchase-order/reference-data')
  },
  createOrUpdatePurchase(data: any) {
    return request.Post<Service.ResponseResult<any>>('/api/purchase-order/create-or-update', data)
  },
  getPurchaseOrder(purchaseOrderId: string) {
    return request.Get<Service.ResponseResult<any>>(`/api/purchase-order/${purchaseOrderId}`)
  },
}
