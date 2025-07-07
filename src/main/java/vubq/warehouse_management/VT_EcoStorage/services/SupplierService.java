package vubq.warehouse_management.VT_EcoStorage.services;

import org.springframework.data.domain.Page;
import vubq.warehouse_management.VT_EcoStorage.dtos.SupplierDto;
import vubq.warehouse_management.VT_EcoStorage.entities.Supplier;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableRequest;

public interface SupplierService {

    Page<Supplier> getSuppliers(DataTableRequest dataTableRequest);

    SupplierDto createOrUpdateSupplier(SupplierDto supplierDto);

    SupplierDto getSupplier(String supplierId);
}
