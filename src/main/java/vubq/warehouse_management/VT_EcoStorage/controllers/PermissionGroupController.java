package vubq.warehouse_management.VT_EcoStorage.controllers;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vubq.warehouse_management.VT_EcoStorage.dtos.requests.PermissionGroupRequest;
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.PermissionGroupResponse;
import vubq.warehouse_management.VT_EcoStorage.services.PermissionGroupService;
import vubq.warehouse_management.VT_EcoStorage.utils.https.Response;

import java.util.Collection;

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

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('PERMISSION.VIEW')")
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean hasEdit = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("PERMISSION.EDIT"));
        boolean hasAdd = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("PERMISSION.ADD"));
        boolean isSuperAdmin = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN.SUPER"));

        if (!StringUtils.isEmpty(permissionGroupRequest.getId())) {
            if (!hasEdit && !isSuperAdmin) {
                throw new IllegalArgumentException("Không có quyền");
            }
        } else {
            if (!hasAdd && !isSuperAdmin) {
                throw new IllegalArgumentException("Không có quyền");
            }
        }
        PermissionGroupResponse permissionGroupResponse = this.permissionGroupService
                .createOrUpdatePermissionGroup(permissionGroupRequest);
        return Response.success(permissionGroupResponse);
    }
}
