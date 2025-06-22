package vubq.warehouse_management.VT_EcoStorage.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "tb_floors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder
public class Floor extends Base {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "shelf_id")
    private String shelfId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "shelf_id",
            insertable = false,
            updatable = false
    )
    private Shelf shelf;

    public enum Status {
        ACTIVE,
        INACTIVE,
    }
}
