package vubq.warehouse_management.VT_EcoStorage.services.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vubq.warehouse_management.VT_EcoStorage.dtos.ProductStatisticalDto;
import vubq.warehouse_management.VT_EcoStorage.repositories.ProductRepository;
import vubq.warehouse_management.VT_EcoStorage.services.StatisticalService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StatisticalServiceImpl implements StatisticalService {

    private final ProductRepository productRepository;

    @Override
    public List<ProductStatisticalDto> getStatistical(Date startDate, Date endDate) {
        List<Object[]> rawResults = productRepository.getWarehouseProductStats(startDate, endDate);

        Map<String, ProductStatisticalDto> warehouseMap = new LinkedHashMap<>();

        for (Object[] row : rawResults) {
            String warehouseId = (String) row[0];
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

            ProductStatisticalDto dto = warehouseMap.computeIfAbsent(warehouseId, id -> ProductStatisticalDto.builder()
                    .warehouseId(warehouseId)
                    .warehouseName(warehouseName)
                    .products(new ArrayList<>())
                    .build());

            dto.getProducts().add(product);
        }

        return new ArrayList<>(warehouseMap.values());
    }
}
