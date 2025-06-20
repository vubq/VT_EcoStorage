package vubq.warehouse_management.VT_EcoStorage.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionGroupRequest {
    private String id;
    private String name;
    private List<String> permissions;
}
