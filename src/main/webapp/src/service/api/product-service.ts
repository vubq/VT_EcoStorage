import { request } from '../http'

export const ProductService = {
  getListProduct(params: DataTable.Request, requestBody: any) {
    return request.Post<Service.ResponseResult<any>>('/api/product/list', requestBody, { params })
  },
  getReferenceDataProduct() {
    return request.Get<Service.ResponseResult<any>>('/api/product/reference-data')
  },
  getListProductInventory(params: DataTable.Request, productId: string) {
    return request.Get<Service.ResponseResult<any>>(`/api/product/product-inventory/${productId}`, { params })
  },
  getProduct(productId: string) {
    return request.Get<Service.ResponseResult<any>>(`/api/product/${productId}`)
  },
  createOrUpdateProduct(data: any) {
    return request.Post<Service.ResponseResult<any>>('/api/product/create-or-update', data)
  },
  getListProductInventoryByLocation(params: DataTable.Request, requestBody: any) {
    return request.Post<Service.ResponseResult<any>>('/api/product/product-by-inventory-location', requestBody, { params })
  },
  getListProductInventoryByLocationId(locationId: string) {
    return request.Get<Service.ResponseResult<any>>(`/api/product/product-by-inventory-location/${locationId}`)
  },
  getListCategory(params: DataTable.Request) {
    return request.Get<Service.ResponseResult<any>>('/api/product/category/list', { params })
  },
  getCategory(id: string) {
    return request.Get<Service.ResponseResult<any>>(`/api/product/category/${id}`)
  },
  createOrUpdateCategory(data: any) {
    return request.Post<Service.ResponseResult<any>>('/api/product/category/create-or-update', data)
  },
  getListUnit(params: DataTable.Request) {
    return request.Get<Service.ResponseResult<any>>('/api/product/unit/list', { params })
  },
  getUnit(id: string) {
    return request.Get<Service.ResponseResult<any>>(`/api/product/unit/${id}`)
  },
  createOrUpdateUnit(data: any) {
    return request.Post<Service.ResponseResult<any>>('/api/product/unit/create-or-update', data)
  },
  getListOrigin(params: DataTable.Request) {
    return request.Get<Service.ResponseResult<any>>('/api/product/origin/list', { params })
  },
  getOrigin(id: string) {
    return request.Get<Service.ResponseResult<any>>(`/api/product/origin/${id}`)
  },
  createOrUpdateOrigin(data: any) {
    return request.Post<Service.ResponseResult<any>>('/api/product/origin/create-or-update', data)
  },
}
