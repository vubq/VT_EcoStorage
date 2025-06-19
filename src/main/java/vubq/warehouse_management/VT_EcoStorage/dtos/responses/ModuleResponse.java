package vubq.warehouse_management.VT_EcoStorage.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vubq.warehouse_management.VT_EcoStorage.dtos.PermissionDto;
import vubq.warehouse_management.VT_EcoStorage.entities.SystemModule;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleResponse {
    private String id;
    private String name;
    private List<PermissionDto> permissions;

    public static ModuleResponse toResponse(SystemModule systemModule) {
        return ModuleResponse.builder()
                .id(systemModule.getId())
                .name(systemModule.getName())
                .permissions(
                        systemModule.getSystemPermissions().stream()
                                .map(PermissionDto::toDto)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
