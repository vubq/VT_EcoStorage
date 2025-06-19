package vubq.warehouse_management.VT_EcoStorage.services;

import org.springframework.data.domain.Page;
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.PermissionGroupResponse;

public interface PermissionGroupManagementService {

    Page<PermissionGroupResponse> getAllPermissionGroups();
}
