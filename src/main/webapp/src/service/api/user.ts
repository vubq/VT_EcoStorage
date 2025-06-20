import { request } from '../http'

export const UserService = {
  getUser(userId: string) {
    return request.Get<Service.ResponseResult<any>>('/api/user/' + userId)
  },
  createOrUpdateUser(data: any) {
    return request.Post<Service.ResponseResult<any>>('/api/user/create-or-update', data)
  },
  getListUser(params: DataTable.Request) {
    return request.Get<Service.ResponseResult<any>>('/api/user/list', { params })
  },
}
