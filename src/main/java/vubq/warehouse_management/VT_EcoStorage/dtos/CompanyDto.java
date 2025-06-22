package vubq.warehouse_management.VT_EcoStorage.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vubq.warehouse_management.VT_EcoStorage.entities.Company;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDto {
    private String id;
    private String name;
    private String code;
    private String taxNumber;
    private String phoneNumber;
    private String email;
    private String address;
    private String contactPerson;
    private String note;
    private String description;
    private Company.Status status;

    public static CompanyDto toDto(Company company) {
        return CompanyDto.builder()
                .id(company.getId())
                .name(company.getName())
                .code(company.getCode())
                .taxNumber(company.getTaxNumber())
                .phoneNumber(company.getPhoneNumber())
                .email(company.getEmail())
                .address(company.getAddress())
                .contactPerson(company.getContactPerson())
                .note(company.getNote())
                .description(company.getDescription())
                .status(company.getStatus())
                .build();
    }
}
