package vubq.warehouse_management.VT_EcoStorage.services.impls;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vubq.warehouse_management.VT_EcoStorage.dtos.*;
import vubq.warehouse_management.VT_EcoStorage.dtos.requests.ProductFilterRequest;
import vubq.warehouse_management.VT_EcoStorage.dtos.requests.ProductInventoryByLocationFilterRequest;
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.ReferenceDataProductResponse;
import vubq.warehouse_management.VT_EcoStorage.entities.*;
import vubq.warehouse_management.VT_EcoStorage.entities.views.ProductByLocation;
import vubq.warehouse_management.VT_EcoStorage.repositories.*;
import vubq.warehouse_management.VT_EcoStorage.services.ProductService;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableRequest;
import vubq.warehouse_management.VT_EcoStorage.utils.specifications.BaseSpecification;
import vubq.warehouse_management.VT_EcoStorage.utils.specifications.SearchCriteria;
import vubq.warehouse_management.VT_EcoStorage.utils.specifications.SearchOperation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    final private ProductCategoryRepository productCategoryRepository;
    final private ProductOriginRepository productOriginRepository;
    final private ProductUnitRepository productUnitRepository;
    final private ProductRepository productRepository;
    final private ProductInventoryRepository productInventoryRepository;
    final private ProductByLocationRepository productByLocationRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public Long getNextProductSeq() {
        return ((Number) entityManager
                .createNativeQuery("SELECT nextval('product_seq')")
                .getSingleResult()).longValue();
    }

    @Override
    public ReferenceDataProductResponse getReferenceDataProduct() {
        ReferenceDataProductResponse response = new ReferenceDataProductResponse();
        response.setProductUnits(
                productUnitRepository.findByStatus(ProductUnit.Status.ACTIVE).stream()
                        .map(ProductUnitDto::toDto).collect(Collectors.toList())
        );
        response.setProductOrigins(
                productOriginRepository.findByStatus(ProductOrigin.Status.ACTIVE).stream()
                        .map(ProductOriginDto::toDto).collect(Collectors.toList())
        );
        response.setProductCategories(
                productCategoryRepository.findByStatus(ProductCategory.Status.ACTIVE).stream()
                        .map(ProductCategoryDto::toDto).collect(Collectors.toList())
        );

        return response;
    }

    @Override
    public boolean createOrUpdateProduct(ProductDto productDto) {
        Product product;
        if (StringUtils.isEmpty(productDto.getId())) {
            product = new Product();
            product.setId("PRODUCT-" + getNextProductSeq());
            product.setSku("SKU-" + String.format("%08d", getProductSeq()));
            product.setBarcode(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + String.format("%06d", getNextProductSeq()));
            product.setStatus(Product.Status.ACTIVE);
            product.setInventoryQuantity(0L);
        } else {
            product = productRepository.findById(productDto.getId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + productDto.getId()));
            product.setStatus(productDto.getStatus());
        }
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setNote(productDto.getNote());
        product.setImageUrl(productDto.getImageUrl());
        product.setCostPrice(productDto.getCostPrice());
        product.setSalePrice(productDto.getSalePrice());
        product.setTaxRate(productDto.getTaxRate());
        product.setProductCategoryId(productDto.getProductCategoryId());
        product.setProductOriginId(productDto.getProductOriginId());
        product.setProductUnitId(productDto.getProductUnitId());
        product = productRepository.saveAndFlush(product);
        return true;
    }

    private Long getProductSeq() {
        return getNextProductSeq();
    }

    @Override
    public Page<Product> getListProduct(
            DataTableRequest dataTableRequest,
            ProductFilterRequest productFilterRequest
    ) {
        PageRequest pageable = dataTableRequest.toPageable();
        BaseSpecification<Product> specNameContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{Base.Fields.name})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        BaseSpecification<Product> specBarcodeContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{Product.Fields.barcode})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        BaseSpecification<Product> specCategoryIdEqual = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{"productCategory", Product.Fields.id})
                        .operation(SearchOperation.EQUALITY)
                        .value(productFilterRequest.getProductCategoryId())
                        .build()
        );
        BaseSpecification<Product> specStatusEqual = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{Product.Fields.status})
                        .operation(SearchOperation.EQUALITY)
                        .value(Product.Status.ACTIVE)
                        .build()
        );
        return productRepository.findAll(
                Specification.where(specStatusEqual)
                        .and(specBarcodeContains)
                        .or(specNameContains)
                        .and(StringUtils.isNotBlank(productFilterRequest.getProductCategoryId()) && !productFilterRequest.getProductCategoryId().equals("ALL") ? specCategoryIdEqual : null),
                pageable);
    }

    @Override
    public Page<ProductCategory> getListProductCategory(DataTableRequest dataTableRequest) {
        PageRequest pageable = dataTableRequest.toPageable();
        BaseSpecification<ProductCategory> specNameContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{Base.Fields.name})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        return productCategoryRepository.findAll(Specification.where(specNameContains), pageable);
    }

    @Override
    public Page<ProductUnit> getListProductUnit(DataTableRequest dataTableRequest) {
        PageRequest pageable = dataTableRequest.toPageable();
        BaseSpecification<ProductUnit> specNameContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{Base.Fields.name})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        return productUnitRepository.findAll(Specification.where(specNameContains), pageable);
    }

    @Override
    public Page<ProductOrigin> getListProductOrigin(DataTableRequest dataTableRequest) {
        PageRequest pageable = dataTableRequest.toPageable();
        BaseSpecification<ProductOrigin> specNameContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{Base.Fields.name})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        return productOriginRepository.findAll(Specification.where(specNameContains), pageable);
    }

    @Override
    public ProductCategoryDto createOrUpdateProductCategory(ProductCategoryDto productCategoryDto) {
        ProductCategory productCategory;
        if (StringUtils.isEmpty(productCategoryDto.getId())) {
            productCategory = new ProductCategory();
            productCategory.setStatus(ProductCategory.Status.ACTIVE);
        } else {
            productCategory = productCategoryRepository.findById(productCategoryDto.getId())
                    .orElseThrow(() -> new RuntimeException("Product category not found with id: " + productCategoryDto.getId()));
            productCategory.setStatus(productCategoryDto.getStatus());
        }
        productCategory.setName(productCategoryDto.getName());
        productCategory = productCategoryRepository.saveAndFlush(productCategory);
        return ProductCategoryDto.toDto(productCategory);
    }

    @Override
    public ProductUnitDto createOrUpdateProductUnit(ProductUnitDto productUnitDto) {
        ProductUnit productUnit;
        if (StringUtils.isEmpty(productUnitDto.getId())) {
            productUnit = new ProductUnit();
            productUnit.setStatus(ProductUnit.Status.ACTIVE);
        } else {
            productUnit = productUnitRepository.findById(productUnitDto.getId())
                    .orElseThrow(() -> new RuntimeException("Product unit not found with id: " + productUnitDto.getId()));
            productUnit.setStatus(productUnitDto.getStatus());
        }
        productUnit.setName(productUnitDto.getName());
        productUnit = productUnitRepository.saveAndFlush(productUnit);
        return ProductUnitDto.toDto(productUnit);
    }

    @Override
    public ProductOriginDto createOrUpdateProductOrigin(ProductOriginDto productOriginDto) {
        ProductOrigin productOrigin;
        if (StringUtils.isEmpty(productOriginDto.getId())) {
            productOrigin = new ProductOrigin();
            productOrigin.setStatus(ProductOrigin.Status.ACTIVE);
        } else {
            productOrigin = productOriginRepository.findById(productOriginDto.getId())
                    .orElseThrow(() -> new RuntimeException("Product origin not found with id: " + productOriginDto.getId()));
            productOrigin.setStatus(productOriginDto.getStatus());
        }
        productOrigin.setName(productOriginDto.getName());
        productOrigin = productOriginRepository.saveAndFlush(productOrigin);
        return ProductOriginDto.toDto(productOrigin);
    }

    @Override
    public ProductCategoryDto getProductCategoryById(String id) {
        ProductCategory productCategory = productCategoryRepository.findById(id).orElse(null);
        if (productCategory == null) {
            throw new RuntimeException("Product category not found with id: " + id);
        }
        return ProductCategoryDto.toDto(productCategory);
    }

    @Override
    public ProductUnitDto getProductUnitById(String id) {
        ProductUnit productUnit = productUnitRepository.findById(id).orElse(null);
        if (productUnit == null) {
            throw new RuntimeException("Product unit not found with id: " + id);
        }
        return ProductUnitDto.toDto(productUnit);
    }

    @Override
    public ProductOriginDto getProductOriginById(String id) {
        ProductOrigin productOrigin = productOriginRepository.findById(id).orElse(null);
        if (productOrigin == null) {
            throw new RuntimeException("Product origin not found with id: " + id);
        }
        return ProductOriginDto.toDto(productOrigin);
    }

    @Override
    public ProductDto getProduct(String productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        return ProductDto.toDto(product);
    }

    @Override
    public Page<ProductInventory> getListProductInventory(DataTableRequest dataTableRequest, String productId) {
        PageRequest pageable = dataTableRequest.toPageable();
        return productInventoryRepository.findByProductId(productId, pageable);
    }

    @Override
    public Page<ProductByLocation> getListProductInventoryByLocation(DataTableRequest dataTableRequest, ProductInventoryByLocationFilterRequest productInventoryByLocationFilterRequest) {
        PageRequest pageable = dataTableRequest.toPageable();
        BaseSpecification<ProductByLocation> specProductNameContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{ProductByLocation.Fields.productName})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        BaseSpecification<ProductByLocation> specProductBarcodeContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{ProductByLocation.Fields.productBarcode})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        BaseSpecification<ProductByLocation> specProductCategoryIdEquality = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{ProductByLocation.Fields.productCategoryId})
                        .operation(SearchOperation.EQUALITY)
                        .value(productInventoryByLocationFilterRequest.getProductCategoryId())
                        .build()
        );
        BaseSpecification<ProductByLocation> specZoneIdEquality = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{ProductByLocation.Fields.zoneId})
                        .operation(SearchOperation.EQUALITY)
                        .value(productInventoryByLocationFilterRequest.getZoneId())
                        .build()
        );
        BaseSpecification<ProductByLocation> specShelfIdEquality = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{ProductByLocation.Fields.shelfId})
                        .operation(SearchOperation.EQUALITY)
                        .value(productInventoryByLocationFilterRequest.getShelfId())
                        .build()
        );
        BaseSpecification<ProductByLocation> specFloorIdEquality = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{ProductByLocation.Fields.floorId})
                        .operation(SearchOperation.EQUALITY)
                        .value(productInventoryByLocationFilterRequest.getFloorId())
                        .build()
        );
        BaseSpecification<ProductByLocation> specWarehouseIdEquality = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{ProductByLocation.Fields.warehouseId})
                        .operation(SearchOperation.EQUALITY)
                        .value(productInventoryByLocationFilterRequest.getWarehouseId())
                        .build()
        );
        return productByLocationRepository.findAll(
                Specification.where(specProductNameContains)
                        .or(specProductBarcodeContains)
                        .and(StringUtils.isNotBlank(productInventoryByLocationFilterRequest.getWarehouseId()) && !productInventoryByLocationFilterRequest.getWarehouseId().equals("ALL") ? specWarehouseIdEquality : null)
                        .and(StringUtils.isNotBlank(productInventoryByLocationFilterRequest.getProductCategoryId()) && !productInventoryByLocationFilterRequest.getProductCategoryId().equals("ALL") ? specProductCategoryIdEquality : null)
                        .and(StringUtils.isNotBlank(productInventoryByLocationFilterRequest.getZoneId()) && !productInventoryByLocationFilterRequest.getZoneId().equals("ALL") ? specZoneIdEquality : null)
                        .and(StringUtils.isNotBlank(productInventoryByLocationFilterRequest.getShelfId()) && !productInventoryByLocationFilterRequest.getShelfId().equals("ALL") ? specShelfIdEquality : null)
                        .and(StringUtils.isNotBlank(productInventoryByLocationFilterRequest.getFloorId()) && !productInventoryByLocationFilterRequest.getFloorId().equals("ALL") ? specFloorIdEquality : null)
                ,
                pageable);
    }

    @Override
    public List<ProductByLocation> getListProductInventoryByLocation(String locationId) {
        return productByLocationRepository.findByFloorId(locationId);
    }

    @Override
    public List<SummaryDto> statisticalProduct(Date from, Date to) {
        List<ProductStatistics> receivedStats = productRepository.findReceivedStats(PurchaseOrder.Status.RECEIVED, from, to);
        List<ProductStatistics> deliveredStats = productRepository.findDeliveredStats(ExportOrder.Status.DELIVERED, from, to);

        Map<String, SummaryDto> summaryMap = new HashMap<>();
        for (ProductStatistics r : receivedStats) {
            summaryMap.put(
                    r.getProductId(),
                    new SummaryDto(
                            r.getProductId(),
                            r.getQuantity(),
                            r.getTotalAmount(),
                            0L,
                            BigDecimal.ZERO,
                            BigDecimal.ZERO
                    )
            );
        }
        for (ProductStatistics d : deliveredStats) {
            summaryMap.merge(
                    d.getProductId(),
                    new SummaryDto(
                            d.getProductId(),
                            0L,
                            BigDecimal.ZERO,
                            d.getQuantity(),
                            d.getTotalAmount(),
                            BigDecimal.ZERO
                    ),
                    (oldVal, newVal) -> {
                        oldVal.setExportQuantity(newVal.getExportQuantity());
                        oldVal.setExportTotal(newVal.getExportTotal());
                        return oldVal;
                    }
            );
        }
        summaryMap.values().forEach(dto -> {
            BigDecimal importTotal = dto.getImportTotal() != null ? dto.getImportTotal() : BigDecimal.ZERO;
            BigDecimal exportTotal = dto.getExportTotal() != null ? dto.getExportTotal() : BigDecimal.ZERO;
            dto.setRevenue(exportTotal.subtract(importTotal));
        });
        return new ArrayList<>(summaryMap.values());
    }
}
