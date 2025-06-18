package vubq.warehouse_management.VT_EcoStorage.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_user_permission_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPermissionGroup {
    @EmbeddedId
    private UserPermissionGroupId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("systemPermissionGroupId")
    @JoinColumn(name = "system_permission_group_id")
    private SystemPermissionGroup systemPermissionGroup;
}

