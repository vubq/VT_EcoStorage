package vubq.warehouse_management.VT_EcoStorage.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "tb_shelves")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder
public class Shelf extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "zone_id")
    private String zoneId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "zone_id",
            insertable = false,
            updatable = false
    )
    private Zone zone;

    public enum Status {
        ACTIVE,
        INACTIVE,
    }
}
