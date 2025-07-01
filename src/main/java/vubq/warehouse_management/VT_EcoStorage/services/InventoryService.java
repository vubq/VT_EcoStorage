package vubq.warehouse_management.VT_EcoStorage.services;

import vubq.warehouse_management.VT_EcoStorage.dtos.InventoryDto;

import java.util.List;

public interface InventoryService {

    List<InventoryDto> getAllInventory();
}
