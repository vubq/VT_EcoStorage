import { request } from '../http'

export const ProductService = {
  getListProduct(params: DataTable.Request, requestBody: any) {
    return request.Post<Service.ResponseResult<any>>('/api/product/list', requestBody, { params })
  },
  getReferenceDataProduct() {
    return request.Get<Service.ResponseResult<any>>('/api/product/reference-data')
  },
  getListProductInventory(params: DataTable.Request, productId: string) {
    return request.Get<Service.ResponseResult<any>>('/api/product/product-inventory/' + productId, { params })
  },
  getProduct(productId: string) {
    return request.Get<Service.ResponseResult<any>>('/api/product/' + productId)
  },
  createOrUpdateProduct(data: any) {
    return request.Post<Service.ResponseResult<any>>('/api/product/create-or-update', data)
  },
}
