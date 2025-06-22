import { request } from '../http'

export const ProductService = {
  getListProduct(params: DataTable.Request, requestBody: any) {
    return request.Post<Service.ResponseResult<any>>('/api/product/list', requestBody, { params })
  },
}
