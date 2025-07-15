package vubq.warehouse_management.VT_EcoStorage.controllers;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vubq.warehouse_management.VT_EcoStorage.dtos.UserDto;
import vubq.warehouse_management.VT_EcoStorage.entities.User;
import vubq.warehouse_management.VT_EcoStorage.services.PermissionGroupService;
import vubq.warehouse_management.VT_EcoStorage.services.UserService;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableRequest;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableResponse;
import vubq.warehouse_management.VT_EcoStorage.utils.https.Response;

import java.util.Collection;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PermissionGroupService permissionGroupService;

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('USER.VIEW')")
    @GetMapping("/{userId}")
    public Response getUser(@PathVariable("userId") String userId) {
        return Response.success(userService.getUser(userId));
    }

    @PostMapping("/create-or-update")
    public Response createOrUpdateUser(@Valid @RequestBody UserDto userDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean hasEdit = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("USER.EDIT"));
        boolean hasAdd = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("USER.ADD"));
        boolean isSuperAdmin = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN.SUPER"));

        if (!StringUtils.isEmpty(userDto.getId())) {
            if (!hasEdit && !isSuperAdmin) {
                throw new IllegalArgumentException("Không có quyền");
            }
        } else {
            if (!hasAdd && !isSuperAdmin) {
                throw new IllegalArgumentException("Không có quyền");
            }
        }
        return Response.success(userService.createOrUpdateUser(userDto));
    }

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('USER.VIEW')")
    @GetMapping("/list")
    public Response getListUser(@NonNull DataTableRequest dataTableRequest, @RequestParam String status) {
        Page<User> results = userService.getListUser(dataTableRequest, status);
        return Response.success(
                DataTableResponse.builder()
                        .list(results.getContent().stream().map(UserDto::toDto).toList())
                        .totalRecords(results.getTotalElements())
                        .build()
        );
    }
}
