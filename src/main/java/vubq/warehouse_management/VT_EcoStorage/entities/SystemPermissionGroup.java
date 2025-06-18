package vubq.warehouse_management.VT_EcoStorage.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "tb_system_permission_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemPermissionGroup {
    @Id
    private String id;

    private String name;

    @OneToMany(mappedBy = "systemPermissionGroup", fetch = FetchType.EAGER)
    private List<SystemPermissionGroupDetail> groupDetails;

    @Transient
    public List<SystemPermission> getPermissions() {
        return groupDetails.stream()
                .map(SystemPermissionGroupDetail::getSystemPermission)
                .collect(Collectors.toList());
    }
}


