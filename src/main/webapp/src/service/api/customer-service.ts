import { request } from '../http'

export const CustomerService = {
  getCustomers(params: DataTable.Request) {
    return request.Get<Service.ResponseResult<any>>('/api/customer/list', { params })
  },
  getCustomer(id: string) {
    return request.Get<Service.ResponseResult<any>>(`/api/customer/${id}`)
  },
  createOrUpdateCustomer(data: any) {
    return request.Post<Service.ResponseResult<any>>('/api/customer/create-or-update', data)
  },
  getExportOrders(params: DataTable.Request, customerId: string) {
    return request.Get<Service.ResponseResult<any>>(`/api/customer/export-orders/${customerId}`, { params })
  },
}
