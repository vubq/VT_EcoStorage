package vubq.warehouse_management.VT_EcoStorage.services.impls;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.PermissionGroupResponse;
import vubq.warehouse_management.VT_EcoStorage.services.PermissionGroupManagementService;

@Service
public class PermissionGroupManagementServiceImpl implements PermissionGroupManagementService {

    @Override
    public Page<PermissionGroupResponse> getAllPermissionGroups() {
        return null;
    }
}
