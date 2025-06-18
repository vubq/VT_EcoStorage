package vubq.warehouse_management.VT_EcoStorage.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_system_permission_group_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemPermissionGroupDetail {
    @EmbeddedId
    private SystemPermissionGroupDetailId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("systemPermissionGroupId")
    @JoinColumn(name = "system_permission_group_id")
    private SystemPermissionGroup systemPermissionGroup;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("systemPermissionId")
    @JoinColumn(name = "system_permission_id")
    private SystemPermission systemPermission;
}


