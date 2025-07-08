import { request } from '../http'

export const InventoryService = {
  getInventory(warehouseId: string, productCategoryId: string, keyword: string) {
    return request.Get<Service.ResponseResult<any>>('/api/inventory?warehouseId=' + warehouseId + '&productCategoryId=' + productCategoryId + '&keyword=' + keyword)
  },
  getReferenceData() {
    return request.Get<Service.ResponseResult<any>>('/api/inventory/reference-data')
  },
}
