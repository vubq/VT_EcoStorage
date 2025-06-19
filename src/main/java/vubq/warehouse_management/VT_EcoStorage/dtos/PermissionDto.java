package vubq.warehouse_management.VT_EcoStorage.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vubq.warehouse_management.VT_EcoStorage.entities.SystemPermission;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionDto {
    private String id;
    private String action;

    public static PermissionDto toDto(SystemPermission systemPermission) {
        return PermissionDto.builder()
                .id(systemPermission.getId())
                .action(systemPermission.getAction())
                .build();
    }
}
