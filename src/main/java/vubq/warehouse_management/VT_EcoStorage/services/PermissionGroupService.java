package vubq.warehouse_management.VT_EcoStorage.services;

import org.springframework.data.domain.Page;
import vubq.warehouse_management.VT_EcoStorage.dtos.PermissionGroupDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.requests.PermissionGroupRequest;
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.ModuleResponse;
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.PermissionGroupResponse;

import java.util.List;

public interface PermissionGroupService {

    Page<PermissionGroupResponse> getAllPermissionGroups();

    List<ModuleResponse> getListModule();

    List<PermissionGroupDto> getListPermissionGroup();

    PermissionGroupResponse getListPermissionByGroup(String permissionGroupId);

    PermissionGroupResponse createOrUpdatePermissionGroup(PermissionGroupRequest request);
}
