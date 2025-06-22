package vubq.warehouse_management.VT_EcoStorage.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "tb_warehouses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder
public class Warehouse extends Base {

    @Id
    private String id;

    private String address;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "company_id")
    private String companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "company_id",
            insertable = false,
            updatable = false
    )
    private Company company;

    public enum Status {
        ACTIVE,
        INACTIVE,
    }
}
