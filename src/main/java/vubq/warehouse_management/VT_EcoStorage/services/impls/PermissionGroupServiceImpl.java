package vubq.warehouse_management.VT_EcoStorage.services.impls;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vubq.warehouse_management.VT_EcoStorage.dtos.PermissionGroupDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.requests.PermissionGroupRequest;
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.ModuleResponse;
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.PermissionGroupResponse;
import vubq.warehouse_management.VT_EcoStorage.entities.*;
import vubq.warehouse_management.VT_EcoStorage.repositories.SystemModuleRepository;
import vubq.warehouse_management.VT_EcoStorage.repositories.SystemPermissionGroupDetailRepository;
import vubq.warehouse_management.VT_EcoStorage.repositories.SystemPermissionGroupRepository;
import vubq.warehouse_management.VT_EcoStorage.repositories.SystemPermissionRepository;
import vubq.warehouse_management.VT_EcoStorage.services.PermissionGroupService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionGroupServiceImpl implements PermissionGroupService {

    private final SystemModuleRepository systemModuleRepository;
    private final SystemPermissionRepository systemPermissionRepository;
    private final SystemPermissionGroupRepository systemPermissionGroupRepository;
    private final SystemPermissionGroupDetailRepository systemPermissionGroupDetailRepository;

    @Override
    public Page<PermissionGroupResponse> getAllPermissionGroups() {
        return null;
    }

    @Override
    public List<ModuleResponse> getListModule() {
        return systemModuleRepository.findAllByStatus(SystemModule.Status.SHOW).stream()
                .map(ModuleResponse::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionGroupDto> getListPermissionGroup() {
        return this.systemPermissionGroupRepository.findAllByStatus(SystemPermissionGroup.Status.ACTIVE).stream()
                .map(PermissionGroupDto::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PermissionGroupResponse getListPermissionByGroup(String permissionGroupId) {
        PermissionGroupResponse permissionGroupResponse = new PermissionGroupResponse();
        SystemPermissionGroup permissionGroup = systemPermissionGroupRepository.findById(permissionGroupId).orElse(null);
        if (permissionGroup != null) {
            List<SystemPermissionGroupDetail> permissionGroupDetails = this.systemPermissionGroupDetailRepository
                    .findAllById_SystemPermissionGroupId(permissionGroupId);
            List<String> permissionIds = permissionGroupDetails.stream()
                    .map(detail -> detail.getSystemPermission().getId())
                    .toList();
            permissionGroupResponse.setId(permissionGroupId);
            permissionGroupResponse.setName(permissionGroup.getName());
            permissionGroupResponse.setPermissions(permissionIds);
        }
        return permissionGroupResponse;
    }

    @Override
    public PermissionGroupResponse createOrUpdatePermissionGroup(PermissionGroupRequest request) {
        PermissionGroupResponse permissionGroupResponse = new PermissionGroupResponse();
        SystemPermissionGroup permissionGroup;

        if (StringUtils.isEmpty(request.getId())) {
            permissionGroup = new SystemPermissionGroup();
            permissionGroup.setStatus(SystemPermissionGroup.Status.ACTIVE);
        } else {
            permissionGroup = systemPermissionGroupRepository.findById(request.getId())
                    .orElseThrow(() -> new RuntimeException("Permission group not found with id: " + request.getId()));
        }

        permissionGroup.setName(request.getName());
        permissionGroup = this.systemPermissionGroupRepository.saveAndFlush(permissionGroup);

        systemPermissionGroupDetailRepository.deleteBySystemPermissionGroupId(permissionGroup.getId());
        systemPermissionGroupDetailRepository.flush();

        SystemPermissionGroup finalPermissionGroup = permissionGroup;
        List<SystemPermissionGroupDetail> permissionGroupDetails = request.getPermissions().stream()
                .map(permission -> SystemPermissionGroupDetail.builder()
                        .id(new SystemPermissionGroupDetailId(finalPermissionGroup.getId(), permission))
                        .systemPermissionGroup(finalPermissionGroup)
                        .systemPermission(SystemPermission.builder().id(permission).build())
                        .build())
                .collect(Collectors.toList());

        this.systemPermissionGroupDetailRepository.saveAllAndFlush(permissionGroupDetails);

        permissionGroupResponse.setId(permissionGroup.getId());
        permissionGroupResponse.setName(permissionGroup.getName());
        return permissionGroupResponse;
    }

}
