package vubq.warehouse_management.VT_EcoStorage.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "tb_product_inventories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder
public class ProductInventory extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private Long quantity;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "product_id")
    private String productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            insertable = false,
            updatable = false
    )
    private Product product;

    @Column(name = "purchase_order_detail_id")
    private String purchaseOrderDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "purchase_order_detail_id",
            insertable = false,
            updatable = false
    )
    private PurchaseOrderDetail purchaseOrderDetail;

    @Column(name = "export_order_detail_id")
    private String exportOrderDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "export_order_detail_id",
            insertable = false,
            updatable = false
    )
    private ExportOrderDetail exportOrderDetail;

    public enum TransactionType {
        ADD,
        SUBTRACT,
        MOVE
    }

    public enum Type {
        PURCHASE_ORDER,
        EXPORT_ORDER,
        ADJUST,
        MOVE_LOCATION
    }
}
