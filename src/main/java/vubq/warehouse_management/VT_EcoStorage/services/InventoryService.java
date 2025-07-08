package vubq.warehouse_management.VT_EcoStorage.services;

import vubq.warehouse_management.VT_EcoStorage.dtos.InventoryDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.ReferenceDataInventoryResponse;

import java.util.List;

public interface InventoryService {

    List<InventoryDto> getAllInventory(String warehouseId, String productCategoryId, String keyword);

    ReferenceDataInventoryResponse getReferenceDataInventory();
}
