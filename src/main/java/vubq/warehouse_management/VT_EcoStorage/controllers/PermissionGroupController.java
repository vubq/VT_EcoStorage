package vubq.warehouse_management.VT_EcoStorage.controllers;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import vubq.warehouse_management.VT_EcoStorage.dtos.requests.PermissionGroupRequest;
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.PermissionGroupResponse;
import vubq.warehouse_management.VT_EcoStorage.services.PermissionGroupService;
import vubq.warehouse_management.VT_EcoStorage.utils.https.Response;

@RestController
@RequestMapping("/api/permission-group")
@RequiredArgsConstructor
public class PermissionGroupController {

    private final PermissionGroupService permissionGroupService;

    @GetMapping("/get-list-module")
    public Response getListModule() {
        return Response.success(
                this.permissionGroupService.getListModule()
        );
    }

    @GetMapping("/get-list-permission-group")
    public Response getListPermissionGroup() {
        return Response.success(
                this.permissionGroupService.getListPermissionGroup()
        );
    }

    @GetMapping("/get-list-permission-by-group/{permissionGroupId}")
    public Response getListPermissionByGroup(@PathVariable String permissionGroupId) {
        PermissionGroupResponse permissionGroupResponse = this.permissionGroupService
                .getListPermissionByGroup(permissionGroupId);
        if (StringUtils.isEmpty(permissionGroupResponse.getId())) {
            return Response.badRequest("Permission group not found");
        }
        return Response.success(
                permissionGroupResponse
        );
    }

    @PostMapping("/create-or-update")
    public Response createOrUpdatePermissionGroup(@RequestBody PermissionGroupRequest permissionGroupRequest) {
        PermissionGroupResponse permissionGroupResponse = this.permissionGroupService
                .createOrUpdatePermissionGroup(permissionGroupRequest);
        return Response.success(permissionGroupResponse);
    }
}
