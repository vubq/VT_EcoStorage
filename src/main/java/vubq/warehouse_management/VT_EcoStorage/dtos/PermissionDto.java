package vubq.warehouse_management.VT_EcoStorage.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionDto {

    private String id;
    private String action;
}
