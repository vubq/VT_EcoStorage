import { request } from '../http'

export const WarehouseService = {
  getListWarehouse() {
    return request.Get<Service.ResponseResult<any>>('/api/warehouse/list')
  },
  getWarehouse(warehouseId: string) {
    return request.Get<Service.ResponseResult<any>>(`/api/warehouse/${warehouseId}`)
  },
  createOrUpdateZone(data: any) {
    return request.Post<Service.ResponseResult<any>>('/api/warehouse/create-or-update-zone', data)
  },
  createOrUpdateShelf(data: any) {
    return request.Post<Service.ResponseResult<any>>('/api/warehouse/create-or-update-shelf', data)
  },
  createOrUpdateFloor(data: any) {
    return request.Post<Service.ResponseResult<any>>('/api/warehouse/create-or-update-floor', data)
  },
}
