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
public class SystemPermissionGroup extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String name;

    @OneToMany(mappedBy = "systemPermissionGroup", fetch = FetchType.EAGER)
    private List<SystemPermissionGroupDetail> groupDetails;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        ACTIVE,
        INACTIVE
    }

    @Transient
    public List<SystemPermission> getPermissions() {
        return groupDetails.stream()
                .map(SystemPermissionGroupDetail::getSystemPermission)
                .collect(Collectors.toList());
    }
}


