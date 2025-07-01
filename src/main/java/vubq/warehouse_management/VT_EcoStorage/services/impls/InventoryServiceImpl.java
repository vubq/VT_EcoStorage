package vubq.warehouse_management.VT_EcoStorage.services.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vubq.warehouse_management.VT_EcoStorage.dtos.InventoryDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.InventoryLocationDto;
import vubq.warehouse_management.VT_EcoStorage.repositories.ProductInventoryLocationRepository;
import vubq.warehouse_management.VT_EcoStorage.services.InventoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ProductInventoryLocationRepository productInventoryLocationRepository;

    @Override
    public List<InventoryDto> getAllInventory() {
        Map<String, Map<String, List<InventoryLocationDto>>> grouped = productInventoryLocationRepository
                .findAllInventoryWithLocation().stream()
                .collect(Collectors.groupingBy(
                        InventoryLocationDto::getWarehouseId,
                        Collectors.groupingBy(InventoryLocationDto::getProductId)
                ));

        List<InventoryDto> result = new ArrayList<>();

        for (Map.Entry<String, Map<String, List<InventoryLocationDto>>> warehouseEntry : grouped.entrySet()) {
            String warehouseId = warehouseEntry.getKey();
            Map<String, List<InventoryLocationDto>> productMap = warehouseEntry.getValue();

            String warehouseName = productMap.values().stream()
                    .flatMap(List::stream)
                    .map(InventoryLocationDto::getWarehouseName)
                    .findFirst()
                    .orElse(null);

            List<InventoryDto.Product> products = new ArrayList<>();

            for (Map.Entry<String, List<InventoryLocationDto>> productEntry : productMap.entrySet()) {
                List<InventoryLocationDto> locations = productEntry.getValue();

                InventoryLocationDto first = locations.get(0);

                List<InventoryDto.Product.Location> locationList = locations.stream()
                        .map(loc -> new InventoryDto.Product.Location(
                                loc.getFloorId(),
                                loc.getZoneName() + " - " + loc.getShelfName() + " - " + loc.getFloorNumber(),
                                loc.getInventoryQuantity()
                        ))
                        .toList();

                long totalQuantity = locations.stream()
                        .mapToLong(InventoryLocationDto::getInventoryQuantity)
                        .sum();

                InventoryDto.Product product = new InventoryDto.Product(
                        first.getProductId(),
                        first.getProductName(),
                        first.getProductBarcode(),
                        first.getProductSKU(),
                        totalQuantity,
                        locationList
                );

                products.add(product);
            }

            InventoryDto inventoryDto = new InventoryDto(warehouseId, warehouseName, products);
            result.add(inventoryDto);
        }
        return result;
    }
}
