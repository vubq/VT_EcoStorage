import { request } from '../http'

export const PermissionGroupService = {
  getListModule() {
    return request.Get<Service.ResponseResult<any>>('/api/permission-group/get-list-module')
  },
  getListPermissionGroup() {
    return request.Get<Service.ResponseResult<any>>('/api/permission-group/get-list-permission-group')
  },
  getListPermissionByGroup(permissionGroupId: string) {
    return request.Get<Service.ResponseResult<any>>(`/api/permission-group/get-list-permission-by-group/${permissionGroupId}`)
  },
  createOrUpdatePermissionGroup(data: any) {
    return request.Post<Service.ResponseResult<any>>('/api/permission-group/create-or-update', data)
  },
}
