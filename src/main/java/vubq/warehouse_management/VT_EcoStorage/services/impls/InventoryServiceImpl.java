package vubq.warehouse_management.VT_EcoStorage.services.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vubq.warehouse_management.VT_EcoStorage.dtos.*;
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.ReferenceDataInventoryResponse;
import vubq.warehouse_management.VT_EcoStorage.entities.Customer;
import vubq.warehouse_management.VT_EcoStorage.entities.ProductCategory;
import vubq.warehouse_management.VT_EcoStorage.entities.Warehouse;
import vubq.warehouse_management.VT_EcoStorage.repositories.ProductCategoryRepository;
import vubq.warehouse_management.VT_EcoStorage.repositories.ProductInventoryLocationRepository;
import vubq.warehouse_management.VT_EcoStorage.repositories.WarehouseRepository;
import vubq.warehouse_management.VT_EcoStorage.services.InventoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ProductInventoryLocationRepository productInventoryLocationRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Override
    public List<InventoryDto> getAllInventory(String warehouseId, String productCategoryId, String keyword) {
        List<InventoryLocationDto> rawData = productInventoryLocationRepository
                .findAllInventoryWithLocation(warehouseId, productCategoryId, keyword);

        Map<String, Map<String, List<InventoryLocationDto>>> grouped = rawData.stream()
                .collect(Collectors.groupingBy(
                        InventoryLocationDto::getWarehouseId,
                        Collectors.groupingBy(InventoryLocationDto::getProductId)
                ));

        List<InventoryDto> result = new ArrayList<>();

        for (Map.Entry<String, Map<String, List<InventoryLocationDto>>> warehouseEntry : grouped.entrySet()) {
            String wId = warehouseEntry.getKey();
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
                        first.getProductCategoryId(),
                        first.getProductCategoryName(),
                        first.getProductUnitId(),
                        first.getProductUnitName(),
                        totalQuantity,
                        locationList
                );

                products.add(product);
            }

            result.add(new InventoryDto(wId, warehouseName, products));
        }

        return result;
    }

    @Override
    public ReferenceDataInventoryResponse getReferenceDataInventory() {
        ReferenceDataInventoryResponse referenceDataInventoryResponse = new ReferenceDataInventoryResponse();
        referenceDataInventoryResponse.setWarehouses(warehouseRepository.findByStatus(Warehouse.Status.ACTIVE).stream().map(WarehouseDto::toDto).collect(Collectors.toList()));
        referenceDataInventoryResponse.setCategories(productCategoryRepository.findByStatus(ProductCategory.Status.ACTIVE).stream().map(ProductCategoryDto::toDto).collect(Collectors.toList()));
        return referenceDataInventoryResponse;
    }
}
