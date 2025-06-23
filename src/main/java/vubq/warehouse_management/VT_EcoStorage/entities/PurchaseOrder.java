package vubq.warehouse_management.VT_EcoStorage.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "tb_purchase_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder
public class PurchaseOrder extends Base {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Date expectedDate;
    private Date receivedDate;
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name = "warehouse_id")
    private String warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "warehouse_id",
            insertable = false,
            updatable = false
    )
    private Warehouse warehouse;

    @Column(name = "supplier_id")
    private String supplierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "supplier_id",
            insertable = false,
            updatable = false
    )
    private Supplier supplier;

    public enum Status {
        NEW,
        CONFIRMED,
        RECEIVED,
        CANCELED
    }

    public enum Type {
        INTERNAL,
        PURCHASE,
    }
}
