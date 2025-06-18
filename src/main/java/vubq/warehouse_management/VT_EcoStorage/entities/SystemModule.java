package vubq.warehouse_management.VT_EcoStorage.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tb_system_modules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemModule {
    @Id
    private String id;

    private String name;

    @OneToMany(mappedBy = "systemModule")
    private List<SystemPermission> systemPermissions;
}
