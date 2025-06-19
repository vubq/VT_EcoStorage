package vubq.warehouse_management.VT_EcoStorage.services.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import vubq.warehouse_management.VT_EcoStorage.dtos.PermissionGroupDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.ModuleResponse;
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.PermissionGroupResponse;
import vubq.warehouse_management.VT_EcoStorage.entities.SystemModule;
import vubq.warehouse_management.VT_EcoStorage.repositories.SystemModuleRepository;
import vubq.warehouse_management.VT_EcoStorage.repositories.SystemPermissionGroupRepository;
import vubq.warehouse_management.VT_EcoStorage.repositories.SystemPermissionRepository;
import vubq.warehouse_management.VT_EcoStorage.services.PermissionGroupManagementService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionGroupManagementServiceImpl implements PermissionGroupManagementService {

    private final SystemModuleRepository systemModuleRepository;
    private final SystemPermissionRepository systemPermissionRepository;
    private final SystemPermissionGroupRepository systemPermissionGroupRepository;

    @Override
    public Page<PermissionGroupResponse> getAllPermissionGroups() {
        return null;
    }

    @Override
    public List<ModuleResponse> getListModule() {
        return systemModuleRepository.findAll().stream()
                .map(ModuleResponse::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionGroupDto> getListPermissionGroup() {
        return this.systemPermissionGroupRepository.findAll().stream()
                .map(PermissionGroupDto::toDto)
                .collect(Collectors.toList());
    }
}
