package vubq.warehouse_management.VT_EcoStorage.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "tb_product_origins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder
public class ProductOrigin extends Base {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        ACTIVE,
        INACTIVE,
    }
}