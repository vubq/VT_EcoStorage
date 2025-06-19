import { request } from '../http'

export const PermissionGroupService = {
  getListModule() {
    return request.Get<Service.ResponseResult<any>>('/api/permission-group/get-list-module')
  },
  getListPermissionGroup() {
    return request.Get<Service.ResponseResult<any>>('/api/permission-group/get-list-permission-group')
  },
}
