import { request } from '../http'

export const InventoryService = {
  getInventory() {
    return request.Get<Service.ResponseResult<any>>('/api/inventory')
  },
}
