package vubq.warehouse_management.VT_EcoStorage.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vubq.warehouse_management.VT_EcoStorage.services.InventoryService;
import vubq.warehouse_management.VT_EcoStorage.utils.https.Response;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping()
    public Response getInventory(
            @RequestParam String warehouseId,
            @RequestParam String productCategoryId,
            @RequestParam String keyword
    ) {
        return Response.success(
                inventoryService.getAllInventory(warehouseId, productCategoryId, keyword)
        );
    }

    @GetMapping("/reference-data")
    public Response getReferenceData() {
        return Response.success(
                inventoryService.getReferenceDataInventory()
        );
    }
}
