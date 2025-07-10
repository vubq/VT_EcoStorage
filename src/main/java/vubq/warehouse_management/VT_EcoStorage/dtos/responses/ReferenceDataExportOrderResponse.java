package vubq.warehouse_management.VT_EcoStorage.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vubq.warehouse_management.VT_EcoStorage.dtos.CompanyDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.CustomerDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.ProductCategoryDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.WarehouseDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceDataExportOrderResponse {
    List<CustomerDto> customers;
    List<WarehouseDto> warehouses;
    List<ProductCategoryDto> categories;
    CompanyDto company;
}
