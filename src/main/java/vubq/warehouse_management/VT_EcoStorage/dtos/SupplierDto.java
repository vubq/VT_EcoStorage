package vubq.warehouse_management.VT_EcoStorage.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vubq.warehouse_management.VT_EcoStorage.entities.Supplier;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierDto {

    private String id;
    private String name;
    private String code;
    private String taxNumber;
    private String phoneNumber;
    private String email;
    private String address;
    private String contactPerson;
    private String description;
    private String note;
    private Supplier.Status status;

    public static SupplierDto toDto(Supplier supplier) {
        return SupplierDto.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .code(supplier.getCode())
                .taxNumber(supplier.getTaxNumber())
                .phoneNumber(supplier.getPhoneNumber())
                .email(supplier.getEmail())
                .address(supplier.getAddress())
                .contactPerson(supplier.getContactPerson())
                .description(supplier.getDescription())
                .note(supplier.getNote())
                .status(supplier.getStatus())
                .build();
    }
}
