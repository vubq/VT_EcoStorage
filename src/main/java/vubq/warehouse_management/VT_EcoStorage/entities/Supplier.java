package vubq.warehouse_management.VT_EcoStorage.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "tb_suppliers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder
public class Supplier extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String code;
    private String taxNumber;
    private String phoneNumber;
    private String email;
    private String address;
    private String contactPerson;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        ACTIVE,
        INACTIVE
    }
}
