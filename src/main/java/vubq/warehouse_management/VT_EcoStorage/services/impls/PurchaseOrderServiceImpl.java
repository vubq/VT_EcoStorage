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
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.ReferenceDataPurchaseOrderResponse;
import vubq.warehouse_management.VT_EcoStorage.entities.*;
import vubq.warehouse_management.VT_EcoStorage.repositories.*;
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

            return true;
        }

        if (purchaseOrder.getStatus() == PurchaseOrder.Status.NEW && purchaseOrderDto.getStatus() == PurchaseOrder.Status.CONFIRMED) {
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

            // Validate location
            for (PurchaseOrderDetailDto p : purchaseOrderDto.getDetails()) {
                if (p.getLocations() == null || p.getLocations().isEmpty()) {
                    throw new IllegalArgumentException("Please select the product location to complete the process");
                }
                for (ProductInventoryLocationDto l : p.getLocations()) {
                    if (StringUtils.isBlank(l.getLocationId())) {
                        throw new IllegalArgumentException("Please select the product location to complete the process");
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
            record HistoryData(String productId, String locationId, String purchaseOrderDetailId, Long quantity) {}
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
    public Page<PurchaseOrder> getListPurchaseOrder(DataTableRequest dataTableRequest) {
        PageRequest pageable = dataTableRequest.toPageable();
        BaseSpecification<PurchaseOrder> specIdContains = new BaseSpecification<>(SearchCriteria.builder().keys(new String[]{PurchaseOrder.Fields.id}).operation(SearchOperation.CONTAINS).value(dataTableRequest.getFilter().trim().toUpperCase()).build());
        return purchaseOrderRepository.findAll(Specification.where(specIdContains), pageable);
    }

    @Override
    public ReferenceDataPurchaseOrderResponse getReferenceDataPurchaseOrder() {
        ReferenceDataPurchaseOrderResponse referenceDataPurchaseOrderResponse = new ReferenceDataPurchaseOrderResponse();
        referenceDataPurchaseOrderResponse.setWarehouses(warehouseRepository.findByStatus(Warehouse.Status.ACTIVE).stream().map(WarehouseDto::toDto).collect(Collectors.toList()));
        referenceDataPurchaseOrderResponse.setSuppliers(supplierRepository.findByStatus(Supplier.Status.ACTIVE).stream().map(SupplierDto::toDto).collect(Collectors.toList()));
        referenceDataPurchaseOrderResponse.setCategories(productCategoryRepository.findByStatus(ProductCategory.Status.ACTIVE).stream().map(ProductCategoryDto::toDto).collect(Collectors.toList()));
        return referenceDataPurchaseOrderResponse;
    }
}
