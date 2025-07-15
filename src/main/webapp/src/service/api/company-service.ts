import { request } from '../http'

export const CompanyService = {
  getCompany() {
    return request.Get<Service.ResponseResult<any>>(`/api/company`)
  },
  createOrUpdateCompany(data: any) {
    return request.Post<Service.ResponseResult<any>>('/api/company/create-or-update', data)
  },
}
