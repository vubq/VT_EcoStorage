package vubq.warehouse_management.VT_EcoStorage.services;

import org.springframework.data.domain.Page;
import vubq.warehouse_management.VT_EcoStorage.dtos.PermissionDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.PermissionGroupDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.ModuleResponse;
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.PermissionGroupResponse;

import java.util.List;

public interface PermissionGroupManagementService {

    Page<PermissionGroupResponse> getAllPermissionGroups();

    List<ModuleResponse> getListModule();

    List<PermissionGroupDto> getListPermissionGroup();
}
