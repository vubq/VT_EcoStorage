package vubq.warehouse_management.VT_EcoStorage.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "tb_zones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder
public class Zone extends Base {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "warehouse_id")
    private String warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "warehouse_id",
            insertable = false,
            updatable = false
    )
    private Warehouse warehouse;

    public enum Status {
        ACTIVE,
        INACTIVE,
    }
}
