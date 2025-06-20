package vubq.warehouse_management.VT_EcoStorage.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_system_permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemPermission {
    @Id
    private String id;

    private String action;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "system_module_id")
    private SystemModule systemModule;

    public enum Status {
        HIDE,
        SHOW
    }
}

