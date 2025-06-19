package vubq.warehouse_management.VT_EcoStorage.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vubq.warehouse_management.VT_EcoStorage.entities.SystemPermissionGroup;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionGroupDto {
    private String id;
    private String name;

    public static PermissionGroupDto toDto(SystemPermissionGroup systemPermissionGroup) {
        return PermissionGroupDto.builder()
                .id(systemPermissionGroup.getId())
                .name(systemPermissionGroup.getName())
                .build();
    }
}
