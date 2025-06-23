package vubq.warehouse_management.VT_EcoStorage.services;

import vubq.warehouse_management.VT_EcoStorage.dtos.FloorDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.ShelfDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.WarehouseDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.ZoneDto;

import java.util.List;

public interface WarehouseService {

    List<WarehouseDto> getWarehouses();

    ZoneDto createOrUpdateZone(ZoneDto zoneDto);

    ShelfDto createOrUpdateShelf(ShelfDto shelfDto);

    List<FloorDto> createOrUpdateFloor(FloorDto floorDto);
}
