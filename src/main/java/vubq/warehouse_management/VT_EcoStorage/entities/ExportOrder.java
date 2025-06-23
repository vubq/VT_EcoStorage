package vubq.warehouse_management.VT_EcoStorage.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "tb_export_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder
public class ExportOrder extends Base {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private Status status;
    private Date expectedDate;
    private Date deliveredDate;
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name = "customer_id")
    private String customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "customer_id",
            insertable = false,
            updatable = false
    )
    private Customer customer;

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
        NEW,
        CONFIRMED,
        DELIVERED,
        CANCELED
    }

    public enum Type {
        EXPORT,
        INTERNAL,
        RETURN,
    }
}
