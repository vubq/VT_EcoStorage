package vubq.warehouse_management.VT_EcoStorage.services.impls;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vubq.warehouse_management.VT_EcoStorage.dtos.*;
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.ReferenceDataPurchaseOrderResponse;
import vubq.warehouse_management.VT_EcoStorage.entities.*;
import vubq.warehouse_management.VT_EcoStorage.repositories.*;
import vubq.warehouse_management.VT_EcoStorage.services.ExportOrderService;
import vubq.warehouse_management.VT_EcoStorage.services.PurchaseOrderService;
import vubq.warehouse_management.VT_EcoStorage.utils.dates.DateUtils;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableRequest;
import vubq.warehouse_management.VT_EcoStorage.utils.specifications.BaseSpecification;
import vubq.warehouse_management.VT_EcoStorage.utils.specifications.SearchCriteria;
import vubq.warehouse_management.VT_EcoStorage.utils.specifications.SearchOperation;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    final private PurchaseOrderRepository purchaseOrderRepository;
    final private PurchaseOrderDetailRepository purchaseOrderDetailRepository;
    final private ProductInventoryRepository productInventoryRepository;
    final private ProductInventoryLocationRepository productInventoryLocationRepository;
    final private WarehouseRepository warehouseRepository;
    final private SupplierRepository supplierRepository;
    final private ProductCategoryRepository productCategoryRepository;
    final private ZoneRepository zoneRepository;
    final private ShelfRepository shelfRepository;
    final private FloorRepository floorRepository;
    final private ProductRepository productRepository;
    final private ProductInventoryLocationHistoryRepository productInventoryLocationHistoryRepository;
    final private ExportOrderRepository exportOrderRepository;
    final private ExportOrderService exportOrderService;
    private final CompanyRepository companyRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public Long getNextPurchaseOrderSeq() {
        return ((Number) entityManager.createNativeQuery("SELECT nextval('purchase_order_seq')").getSingleResult()).longValue();
    }

    @Override
    public boolean createOrUpdatePurchaseOrder(PurchaseOrderDto purchaseOrderDto) {
        if (purchaseOrderDto.getDetails() == null || purchaseOrderDto.getDetails().isEmpty()) {
            throw new IllegalArgumentException("No products have been added");
        }
        PurchaseOrder purchaseOrder;
        if (StringUtils.isEmpty(purchaseOrderDto.getId())) {
            purchaseOrder = new PurchaseOrder();
            purchaseOrder.setId(generatePurchaseOrderId(purchaseOrderDto.getSupplierId(), purchaseOrderDto.getWarehouseId()));
            purchaseOrder.setStatus(PurchaseOrder.Status.NEW);
            purchaseOrder.setSupplierId(purchaseOrderDto.getSupplierId());
            purchaseOrder.setWarehouseId(purchaseOrderDto.getWarehouseId());
            purchaseOrder.setExpectedDate(purchaseOrderDto.getExpectedDate());
            purchaseOrder.setType(PurchaseOrder.Type.PURCHASE);

            purchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);

            PurchaseOrder finalPurchaseOrder = purchaseOrder;
            List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrderDto.getDetails().stream().filter(detail -> !detail.isDelete()).map(purchaseOrderDetailDto -> PurchaseOrderDetailDto.toEntity(purchaseOrderDetailDto, finalPurchaseOrder.getId())).toList();

            purchaseOrder.setTotalAmount(purchaseOrderDetails.stream().map(PurchaseOrderDetail::getTotalAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
            purchaseOrderDetailRepository.saveAll(purchaseOrderDetails);

            return true;
        } else {
            purchaseOrder = purchaseOrderRepository.findById(purchaseOrderDto.getId()).orElseThrow(() -> new RuntimeException("Purchase order not found with id: " + purchaseOrderDto.getId()));
        }

        if (purchaseOrder.getStatus() == PurchaseOrder.Status.NEW && purchaseOrderDto.getStatus() == PurchaseOrder.Status.NEW) {
            purchaseOrder.setSupplierId(purchaseOrderDto.getSupplierId());
            purchaseOrder.setWarehouseId(purchaseOrderDto.getWarehouseId());
            purchaseOrder.setExpectedDate(purchaseOrderDto.getExpectedDate());
            purchaseOrder.setNote(purchaseOrderDto.getNote());

            PurchaseOrder finalPurchaseOrder = purchaseOrder;
            List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrderDto.getDetails().stream().filter(detail -> !detail.isDelete()).map(purchaseOrderDetailDto -> PurchaseOrderDetailDto.toEntity(purchaseOrderDetailDto, finalPurchaseOrder.getId())).toList();

            purchaseOrder.setTotalAmount(purchaseOrderDetails.stream().map(PurchaseOrderDetail::getTotalAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));

            purchaseOrderRepository.saveAndFlush(purchaseOrder);
            purchaseOrderDetailRepository.saveAll(purchaseOrderDetails);

            List<String> purchaseOrderDetailDeleteIds = purchaseOrderDto.getDetails().stream().filter(PurchaseOrderDetailDto::isDelete).map(PurchaseOrderDetailDto::getId).filter(StringUtils::isNotBlank).toList();
            purchaseOrderDetailRepository.deleteByIdIn(purchaseOrderDetailDeleteIds);

            return true;
        }

        if (purchaseOrder.getStatus() == PurchaseOrder.Status.NEW && purchaseOrderDto.getStatus() == PurchaseOrder.Status.CANCELED) {
            purchaseOrder.setNote(purchaseOrderDto.getNote());
            purchaseOrder.setStatus(PurchaseOrder.Status.CANCELED);
            purchaseOrderRepository.saveAndFlush(purchaseOrder);

            if (!StringUtils.isEmpty(purchaseOrder.getExportOrderId())) {
                ExportOrder exportOrder = exportOrderRepository.findById(purchaseOrder.getExportOrderId()).orElse(null);
                if (exportOrder != null) {
                    exportOrder.setStatus(ExportOrder.Status.CANCELED);
                    exportOrder.setNote(purchaseOrderDto.getNote());
                    exportOrderRepository.saveAndFlush(exportOrder);
                }
            }
            return true;
        }

        if (purchaseOrder.getStatus() == PurchaseOrder.Status.CONFIRMED && purchaseOrderDto.getStatus() == PurchaseOrder.Status.CANCELED) {
            purchaseOrder.setNote(purchaseOrderDto.getNote());
            purchaseOrder.setStatus(PurchaseOrder.Status.CANCELED);
            purchaseOrderRepository.saveAndFlush(purchaseOrder);

            if (!StringUtils.isEmpty(purchaseOrder.getExportOrderId())) {
                ExportOrder exportOrder = exportOrderRepository.findById(purchaseOrder.getExportOrderId()).orElse(null);
                if (exportOrder != null) {
                    exportOrder.setStatus(ExportOrder.Status.CANCELED);
                    exportOrder.setNote(purchaseOrderDto.getNote());
                    exportOrderRepository.saveAndFlush(exportOrder);
                }
            }
            return true;
        }

        if (purchaseOrder.getStatus() == PurchaseOrder.Status.NEW && purchaseOrderDto.getStatus() == PurchaseOrder.Status.CONFIRMED) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            boolean isSuperAdmin = authorities.stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMIN.SUPER"));

            boolean hasConfirm = authorities.stream()
                    .anyMatch(auth -> auth.getAuthority().equals("PURCHASE_ORDER.CONFIRM"));

            if (!isSuperAdmin) {
                if (!hasConfirm) {
                    throw new IllegalArgumentException("Không có quyền");
                }
            }

            purchaseOrder.setStatus(PurchaseOrder.Status.CONFIRMED);
            purchaseOrder.setSupplierId(purchaseOrderDto.getSupplierId());
            purchaseOrder.setWarehouseId(purchaseOrderDto.getWarehouseId());
            purchaseOrder.setExpectedDate(purchaseOrderDto.getExpectedDate());
            purchaseOrder.setNote(purchaseOrderDto.getNote());

            PurchaseOrder finalPurchaseOrder = purchaseOrder;
            List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrderDto.getDetails().stream().filter(detail -> !detail.isDelete()).map(purchaseOrderDetailDto -> PurchaseOrderDetailDto.toEntity(purchaseOrderDetailDto, finalPurchaseOrder.getId())).toList();

            purchaseOrder.setTotalAmount(purchaseOrderDetails.stream().map(PurchaseOrderDetail::getTotalAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));

            purchaseOrderRepository.saveAndFlush(purchaseOrder);
            purchaseOrderDetailRepository.saveAll(purchaseOrderDetails);

            List<String> purchaseOrderDetailDeleteIds = purchaseOrderDto.getDetails().stream().filter(PurchaseOrderDetailDto::isDelete).map(PurchaseOrderDetailDto::getId).filter(StringUtils::isNotBlank).toList();
            purchaseOrderDetailRepository.deleteByIdIn(purchaseOrderDetailDeleteIds);

            return true;
        }

//        if (purchaseOrder.getStatus() == PurchaseOrder.Status.CONFIRMED && purchaseOrderDto.getStatus() == PurchaseOrder.Status.RECEIVED) {
//
//            // Validate location
//            purchaseOrderDto.getDetails().forEach(p -> {
//                if (p.getLocations() == null || p.getLocations().isEmpty()) {
//                    throw new IllegalArgumentException("Please select the product location to complete the process");
//                }
//                p.getLocations().forEach(l -> {
//                    if (StringUtils.isBlank(l.getLocationId())) {
//                        throw new IllegalArgumentException("Please select the product location to complete the process");
//                    }
//                });
//            });
//
//            purchaseOrder.setStatus(PurchaseOrder.Status.RECEIVED);
//            purchaseOrder.setReceivedDate(DateUtils.getCurrentTime());
//
//            // === Update inventory quantity of products ===
//            List<String> productIds = purchaseOrderDto.getDetails().stream()
//                    .map(PurchaseOrderDetailDto::getProductId).toList();
//            Map<String, Product> productMap = productRepository.findByIdIn(productIds)
//                    .stream().collect(Collectors.toMap(Product::getId, p -> p));
//            List<Product> productsToUpdate = new ArrayList<>();
//
//            // Save ProductInventory (history of transaction)
//            List<ProductInventory> productInventories = purchaseOrderDto.getDetails().stream().map(detail -> {
//                ProductInventory pi = new ProductInventory();
//                pi.setType(ProductInventory.Type.PURCHASE_ORDER);
//                pi.setTransactionType(ProductInventory.TransactionType.ADD);
//                pi.setProductId(detail.getProductId());
//                pi.setPurchaseOrderDetailId(detail.getId());
//                pi.setQuantity(detail.getQuantity());
//
//                // update product's total quantity
//                Product product = productMap.get(detail.getProductId());
//                product.setInventoryQuantity(
//                        product.getInventoryQuantity() + detail.getQuantity()
//                );
//                productsToUpdate.add(product);
//
//                return pi;
//            }).toList();
//            productInventoryRepository.saveAllAndFlush(productInventories);
//            productRepository.saveAllAndFlush(productsToUpdate);
//
//            // === Upsert ProductInventoryLocation ===
//            List<String> locationIds = purchaseOrderDto.getDetails().stream()
//                    .flatMap(detail -> detail.getLocations().stream())
//                    .map(ProductInventoryLocationDto::getLocationId)
//                    .distinct().toList();
//
//            List<ProductInventoryLocation> existingLocations =
//                    productInventoryLocationRepository.findByProductIdInAndLocationIdIn(productIds, locationIds);
//
//            Map<String, ProductInventoryLocation> existingLocationMap = existingLocations.stream()
//                    .collect(Collectors.toMap(
//                            pil -> pil.getProductId() + "_" + pil.getLocationId(),
//                            pil -> pil
//                    ));
//
//            for (PurchaseOrderDetailDto detail : purchaseOrderDto.getDetails()) {
//                for (ProductInventoryLocationDto locationDto : detail.getLocations()) {
//                    String key = detail.getProductId() + "_" + locationDto.getLocationId();
//                    ProductInventoryLocation pil = existingLocationMap.get(key);
//
//                    if (pil != null) {
//                        pil.setInventoryQuantity(pil.getInventoryQuantity() + locationDto.getQuantity());
//                    } else {
//                        pil = new ProductInventoryLocation();
//                        pil.setProductId(detail.getProductId());
//                        pil.setLocationId(locationDto.getLocationId());
//                        pil.setInventoryQuantity(locationDto.getQuantity());
//                        pil.setStatus(ProductInventoryLocation.Status.ACTIVE);
//                        existingLocationMap.put(key, pil); // put into map
//                    }
//                }
//            }
//
//            productInventoryLocationRepository.saveAllAndFlush(new ArrayList<>(existingLocationMap.values()));
//        }

        if (purchaseOrder.getStatus() == PurchaseOrder.Status.CONFIRMED && purchaseOrderDto.getStatus() == PurchaseOrder.Status.RECEIVED) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            boolean isSuperAdmin = authorities.stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMIN.SUPER"));

            boolean hasConfirm = authorities.stream()
                    .anyMatch(auth -> auth.getAuthority().equals("PURCHASE_ORDER.CONFIRM"));

            if (!isSuperAdmin) {
                if (!hasConfirm) {
                    throw new IllegalArgumentException("Không có quyền");
                }
            }

            if (!StringUtils.isEmpty(purchaseOrder.getExportOrderId())) {
                ExportOrder exportOrder = exportOrderRepository.findById(purchaseOrder.getExportOrderId()).orElse(null);
                if (exportOrder != null) {
                    ExportOrderDto exportOrderDto = exportOrderService.getExportOrder(exportOrder.getId());
                    exportOrderDto.setStatus(ExportOrder.Status.DELIVERED);
                    exportOrderDto.setNote(purchaseOrderDto.getNote());
                    exportOrderService.createOrUpdateExportOrder(exportOrderDto);
                }
            }

            // Validate location
            for (PurchaseOrderDetailDto p : purchaseOrderDto.getDetails()) {
                if (p.getLocations() == null || p.getLocations().isEmpty()) {
                    throw new IllegalArgumentException("Vui lòng chọn vị trí để " + p.getProductName() + " [Barcode: " + p.getProductBarcode() + "]");
                }
                for (ProductInventoryLocationDto l : p.getLocations()) {
                    if (StringUtils.isBlank(l.getLocationId())) {
                        throw new IllegalArgumentException("Vui lòng chọn vị trí để " + p.getProductName() + " [Barcode: " + p.getProductBarcode() + "]");
                    }
                }
            }

            purchaseOrder.setStatus(PurchaseOrder.Status.RECEIVED);
            purchaseOrder.setReceivedDate(DateUtils.getCurrentTime());

            // === Update Product inventory ===
            List<String> productIds = purchaseOrderDto.getDetails().stream()
                    .map(PurchaseOrderDetailDto::getProductId)
                    .toList();
            Map<String, Product> productMap = productRepository.findByIdIn(productIds)
                    .stream().collect(Collectors.toMap(Product::getId, p -> p));
            List<Product> productsToUpdate = new ArrayList<>();

            List<ProductInventory> productInventories = new ArrayList<>();
            for (PurchaseOrderDetailDto detail : purchaseOrderDto.getDetails()) {
                ProductInventory pi = new ProductInventory();
                pi.setType(ProductInventory.Type.PURCHASE_ORDER);
                pi.setTransactionType(ProductInventory.TransactionType.ADD);
                pi.setProductId(detail.getProductId());
                pi.setPurchaseOrderDetailId(detail.getId());
                pi.setQuantity(detail.getQuantity());

                // update product's total quantity
                Product product = productMap.get(detail.getProductId());
                product.setInventoryQuantity(
                        product.getInventoryQuantity() + detail.getQuantity()
                );
                productsToUpdate.add(product);

                productInventories.add(pi);
            }
            productInventoryRepository.saveAllAndFlush(productInventories);
            productRepository.saveAllAndFlush(productsToUpdate);

            // === Upsert ProductInventoryLocation ===
            List<String> locationIds = purchaseOrderDto.getDetails().stream()
                    .flatMap(detail -> detail.getLocations().stream())
                    .map(ProductInventoryLocationDto::getLocationId)
                    .distinct().toList();

            List<ProductInventoryLocation> existingLocations =
                    productInventoryLocationRepository.findByProductIdInAndLocationIdIn(productIds, locationIds);

            Map<String, ProductInventoryLocation> existingLocationMap = existingLocations.stream()
                    .collect(Collectors.toMap(
                            pil -> pil.getProductId() + "_" + pil.getLocationId(),
                            pil -> pil
                    ));

            // Prepare a list of histories but without PIL id initially
            record HistoryData(String productId, String locationId, String purchaseOrderDetailId, Long quantity) {
            }
            List<HistoryData> historyDataList = new ArrayList<>();

            for (PurchaseOrderDetailDto detail : purchaseOrderDto.getDetails()) {
                for (ProductInventoryLocationDto locationDto : detail.getLocations()) {
                    String key = detail.getProductId() + "_" + locationDto.getLocationId();
                    ProductInventoryLocation pil = existingLocationMap.get(key);

                    if (pil != null) {
                        pil.setInventoryQuantity(pil.getInventoryQuantity() + locationDto.getQuantity());
                    } else {
                        pil = new ProductInventoryLocation();
                        pil.setProductId(detail.getProductId());
                        pil.setLocationId(locationDto.getLocationId());
                        pil.setInventoryQuantity(locationDto.getQuantity());
                        pil.setStatus(ProductInventoryLocation.Status.ACTIVE);
                        existingLocationMap.put(key, pil);
                    }

                    // Save history data to process after save PILs
                    historyDataList.add(new HistoryData(
                            detail.getProductId(),
                            locationDto.getLocationId(),
                            detail.getId(),
                            locationDto.getQuantity()
                    ));
                }
            }

            // Save PILs
            List<ProductInventoryLocation> savedPILs =
                    productInventoryLocationRepository.saveAllAndFlush(
                            new ArrayList<>(existingLocationMap.values())
                    );

            // Map PILs by productId + locationId for history
            Map<String, ProductInventoryLocation> savedPILMap = savedPILs.stream().collect(
                    Collectors.toMap(
                            pil -> pil.getProductId() + "_" + pil.getLocationId(),
                            pil -> pil
                    )
            );

            // === Save history ===
            List<ProductInventoryLocationHistory> histories = new ArrayList<>();
            for (HistoryData hd : historyDataList) {
                ProductInventoryLocation pil = savedPILMap.get(hd.productId() + "_" + hd.locationId());
                ProductInventoryLocationHistory history = new ProductInventoryLocationHistory();
                history.setProductInventoryLocationId(pil.getId());
                history.setPurchaseOrderDetailId(hd.purchaseOrderDetailId());
                history.setType(ProductInventoryLocationHistory.Type.PURCHASE);
                history.setQuantity(hd.quantity());
                histories.add(history);
            }
            productInventoryLocationHistoryRepository.saveAll(histories);
        }

        return false;
    }

    @Override
    public PurchaseOrderDto getPurchaseOrder(String purchaseOrderId) {
        PurchaseOrderDto purchaseOrderDto = new PurchaseOrderDto();
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId).orElseThrow(() -> new RuntimeException("Purchase order not found with id: " + purchaseOrderId));

        List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrderDetailRepository.findByPurchaseOrderId(purchaseOrderId);
        purchaseOrderDto = PurchaseOrderDto.toDto(purchaseOrder);
        purchaseOrderDto.setDetails(purchaseOrderDetails.stream().map(PurchaseOrderDetailDto::toDto).collect(Collectors.toList()));
        return purchaseOrderDto;
    }

    private String generatePurchaseOrderId(String supplierId, String warehouseId) {
        return "PO" + "-" + getNextPurchaseOrderSeq()
//                + "-" + supplierId
//                + "-" + warehouseId
                + "-S" + "-W" + "-" + new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    @Override
    public Page<PurchaseOrder> getListPurchaseOrder(DataTableRequest dataTableRequest, String warehouseId, String type, String status) {
        PageRequest pageable = dataTableRequest.toPageable();
        BaseSpecification<PurchaseOrder> specIdContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{PurchaseOrder.Fields.id})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        BaseSpecification<PurchaseOrder> specSupplierNameContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{"supplier", Base.Fields.name})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        BaseSpecification<PurchaseOrder> specWarehouseFromNameContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{"warehouseFrom", Base.Fields.name})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        BaseSpecification<PurchaseOrder> specWarehouseIdEqual = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{PurchaseOrder.Fields.warehouseId})
                        .operation(SearchOperation.EQUALITY)
                        .value(warehouseId)
                        .build()
        );
        BaseSpecification<PurchaseOrder> specTypeEqual = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{PurchaseOrder.Fields.type})
                        .operation(SearchOperation.EQUALITY)
                        .value(type)
                        .build()
        );
        BaseSpecification<PurchaseOrder> specStatusEqual = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{PurchaseOrder.Fields.status})
                        .operation(SearchOperation.EQUALITY)
                        .value(status)
                        .build()
        );
        return purchaseOrderRepository.findAll(
                Specification.where(specIdContains)
                        .or(specSupplierNameContains)
                        .or(specWarehouseFromNameContains)
                        .and(warehouseId.equals("ALL") ? null : specWarehouseIdEqual)
                        .and(type.equals("ALL") ? null : specTypeEqual)
                        .and(status.equals("ALL") ? null : specStatusEqual)
                , pageable);
    }

    @Override
    public ReferenceDataPurchaseOrderResponse getReferenceDataPurchaseOrder() {
        ReferenceDataPurchaseOrderResponse referenceDataPurchaseOrderResponse = new ReferenceDataPurchaseOrderResponse();
        referenceDataPurchaseOrderResponse.setWarehouses(warehouseRepository.findByStatus(Warehouse.Status.ACTIVE).stream().map(WarehouseDto::toDto).collect(Collectors.toList()));
        referenceDataPurchaseOrderResponse.setSuppliers(supplierRepository.findByStatus(Supplier.Status.ACTIVE).stream().map(SupplierDto::toDto).collect(Collectors.toList()));
        referenceDataPurchaseOrderResponse.setCategories(productCategoryRepository.findByStatus(ProductCategory.Status.ACTIVE).stream().map(ProductCategoryDto::toDto).collect(Collectors.toList()));
        referenceDataPurchaseOrderResponse.setCompany(CompanyDto.toDto(companyRepository.findAll().get(0)));
        return referenceDataPurchaseOrderResponse;
    }

    @Override
    public Page<PurchaseOrder> getPurchaseOrders(DataTableRequest dataTableRequest, String supplierId) {
        PageRequest pageable = dataTableRequest.toPageable();
        BaseSpecification<PurchaseOrder> specCustomerIdEqual = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{PurchaseOrder.Fields.supplierId})
                        .operation(SearchOperation.EQUALITY)
                        .value(supplierId)
                        .build()
        );
        return purchaseOrderRepository.findAll(Specification.where(specCustomerIdEqual), pageable);
    }
}
