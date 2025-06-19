package vubq.warehouse_management.VT_EcoStorage.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import vubq.warehouse_management.VT_EcoStorage.configs.securities.auths.CustomUserDetails;
import vubq.warehouse_management.VT_EcoStorage.dtos.requests.AuthRequest;
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.AuthResponse;
import vubq.warehouse_management.VT_EcoStorage.services.PermissionGroupManagementService;
import vubq.warehouse_management.VT_EcoStorage.utils.https.Response;

import java.util.List;

@RestController
@RequestMapping("/api/permission-group")
@RequiredArgsConstructor
public class PermissionGroupController {

    private final PermissionGroupManagementService permissionGroupManagementService;

    @GetMapping("/get-list-module")
    public Response getListModule() {
        return Response.success(
                this.permissionGroupManagementService.getListModule()
        );
    }

    @GetMapping("/get-list-permission-group")
    public Response getListPermissionGroup() {
        return Response.success(
                this.permissionGroupManagementService.getListPermissionGroup()
        );
    }
}
