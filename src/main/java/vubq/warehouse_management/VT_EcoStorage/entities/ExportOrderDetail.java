package vubq.warehouse_management.VT_EcoStorage.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_export_order_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder
public class ExportOrderDetail extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private Long quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "product_id")
    private String productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            insertable = false,
            updatable = false
    )
    private Product product;

    @Column(name = "export_order_id")
    private String exportOrderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "export_order_id",
            insertable = false,
            updatable = false
    )
    private ExportOrder exportOrder;

    public enum Status {
        ACTIVE,
        INACTIVE,
    }
}
