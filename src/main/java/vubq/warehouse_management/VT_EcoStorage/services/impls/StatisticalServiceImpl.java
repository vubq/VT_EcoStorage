package vubq.warehouse_management.VT_EcoStorage.services.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vubq.warehouse_management.VT_EcoStorage.dtos.ProductStatisticalDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.WarehouseDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.ReferenceDataStatisticalResponse;
import vubq.warehouse_management.VT_EcoStorage.entities.Warehouse;
import vubq.warehouse_management.VT_EcoStorage.repositories.ProductRepository;
import vubq.warehouse_management.VT_EcoStorage.repositories.WarehouseRepository;
import vubq.warehouse_management.VT_EcoStorage.services.StatisticalService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticalServiceImpl implements StatisticalService {

    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    public List<ProductStatisticalDto> getStatistical(Date startDate, Date endDate, String warehouseId, String keyword, boolean onlyWithTransaction) {
        List<Object[]> rawResults = productRepository.getWarehouseProductStats(startDate, endDate, warehouseId, keyword, onlyWithTransaction);

        Map<String, ProductStatisticalDto> warehouseMap = new LinkedHashMap<>();

        for (Object[] row : rawResults) {
            String warehouseIdData = (String) row[0];
            String warehouseName = (String) row[1];

            String productId = (String) row[2];
            String productName = (String) row[3];
            String productBarcode = (String) row[4];
            String productSKU = (String) row[5];
            Long totalImportQuantity = ((Number) row[6]).longValue();
            Double totalImportAmount = ((Number) row[7]).doubleValue();
            Long totalExportQuantity = ((Number) row[8]).longValue();
            Double totalExportAmount = ((Number) row[9]).doubleValue();

            ProductStatisticalDto.Product product = new ProductStatisticalDto.Product(
                    productId,
                    productName,
                    productBarcode,
                    productSKU,
                    totalImportQuantity,
                    totalImportAmount,
                    totalExportQuantity,
                    totalExportAmount
            );

            ProductStatisticalDto dto = warehouseMap.computeIfAbsent(warehouseIdData, id -> ProductStatisticalDto.builder()
                    .warehouseId(warehouseIdData)
                    .warehouseName(warehouseName)
                    .products(new ArrayList<>())
                    .build());

            dto.getProducts().add(product);
        }

        return new ArrayList<>(warehouseMap.values());
    }

    @Override
    public ReferenceDataStatisticalResponse getReferenceDataStatistical() {
        ReferenceDataStatisticalResponse referenceDataStatisticalResponse = new ReferenceDataStatisticalResponse();
        referenceDataStatisticalResponse.setWarehouses(warehouseRepository.findByStatus(Warehouse.Status.ACTIVE).stream().map(WarehouseDto::toDto).collect(Collectors.toList()));
        return referenceDataStatisticalResponse;
    }
}
