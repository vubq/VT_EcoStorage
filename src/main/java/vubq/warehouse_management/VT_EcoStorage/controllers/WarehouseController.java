package vubq.warehouse_management.VT_EcoStorage.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vubq.warehouse_management.VT_EcoStorage.dtos.FloorDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.ShelfDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.ZoneDto;
import vubq.warehouse_management.VT_EcoStorage.services.WarehouseService;
import vubq.warehouse_management.VT_EcoStorage.utils.https.Response;

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    final private WarehouseService warehouseService;

    @GetMapping("/list")
    public Response getListWarehouse() {
        return Response.success(warehouseService.getWarehouses());
    }

    @GetMapping("/{warehouseId}")
    public Response getWarehouse(@PathVariable("warehouseId") String warehouseId) {
        return Response.success(warehouseService.getWarehouse(warehouseId));
    }

    @PostMapping("/create-or-update-zone")
    public Response createOrUpdateZone(@RequestBody ZoneDto zoneDto) {
        return Response.success(warehouseService.createOrUpdateZone(zoneDto));
    }

    @PostMapping("/create-or-update-shelf")
    public Response createOrUpdateShelf(@RequestBody ShelfDto shelfDto) {
        return Response.success(warehouseService.createOrUpdateShelf(shelfDto));
    }

    @PostMapping("/create-or-update-floor")
    public Response createOrUpdateFloor(@RequestBody FloorDto floorDto) {
        return Response.success(warehouseService.createOrUpdateFloor(floorDto));
    }
}
