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
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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

    @PersistenceContext
    private EntityManager entityManager;

    public Long getNextPurchaseOrderSeq() {
        return ((Number) entityManager
                .createNativeQuery("SELECT nextval('purchase_order_seq')")
                .getSingleResult()).longValue();
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
            List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrderDto.getDetails().stream()
                    .filter(detail -> !detail.isDelete())
                    .map(purchaseOrderDetailDto -> PurchaseOrderDetailDto.toEntity(purchaseOrderDetailDto, finalPurchaseOrder.getId()))
                    .toList();

            purchaseOrder.setTotalAmount(
                    purchaseOrderDetails.stream()
                            .map(PurchaseOrderDetail::getTotalAmount)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
            );
            purchaseOrderDetailRepository.saveAll(purchaseOrderDetails);

            return true;
        } else {
            purchaseOrder = purchaseOrderRepository.findById(purchaseOrderDto.getId())
                    .orElseThrow(() -> new RuntimeException("Purchase order not found with id: " + purchaseOrderDto.getId()));
        }

        if (purchaseOrder.getStatus() == PurchaseOrder.Status.NEW && purchaseOrderDto.getStatus() == PurchaseOrder.Status.NEW) {
            purchaseOrder.setSupplierId(purchaseOrderDto.getSupplierId());
            purchaseOrder.setWarehouseId(purchaseOrderDto.getWarehouseId());
            purchaseOrder.setExpectedDate(purchaseOrderDto.getExpectedDate());
            purchaseOrder.setNote(purchaseOrderDto.getNote());

            PurchaseOrder finalPurchaseOrder = purchaseOrder;
            List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrderDto.getDetails().stream()
                    .filter(detail -> !detail.isDelete())
                    .map(purchaseOrderDetailDto -> PurchaseOrderDetailDto.toEntity(purchaseOrderDetailDto, finalPurchaseOrder.getId()))
                    .toList();

            purchaseOrder.setTotalAmount(
                    purchaseOrderDetails.stream()
                            .map(PurchaseOrderDetail::getTotalAmount)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
            );

            purchaseOrderRepository.saveAndFlush(purchaseOrder);
            purchaseOrderDetailRepository.saveAll(purchaseOrderDetails);

            List<String> purchaseOrderDetailDeleteIds = purchaseOrderDto.getDetails().stream()
                    .filter(PurchaseOrderDetailDto::isDelete)
                    .map(PurchaseOrderDetailDto::getId)
                    .filter(StringUtils::isNotBlank)
                    .toList();
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
            List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrderDto.getDetails().stream()
                    .filter(detail -> !detail.isDelete())
                    .map(purchaseOrderDetailDto -> PurchaseOrderDetailDto.toEntity(purchaseOrderDetailDto, finalPurchaseOrder.getId()))
                    .toList();

            purchaseOrder.setTotalAmount(
                    purchaseOrderDetails.stream()
                            .map(PurchaseOrderDetail::getTotalAmount)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
            );

            purchaseOrderRepository.saveAndFlush(purchaseOrder);
            purchaseOrderDetailRepository.saveAll(purchaseOrderDetails);

            List<String> purchaseOrderDetailDeleteIds = purchaseOrderDto.getDetails().stream()
                    .filter(PurchaseOrderDetailDto::isDelete)
                    .map(PurchaseOrderDetailDto::getId)
                    .filter(StringUtils::isNotBlank)
                    .toList();
            purchaseOrderDetailRepository.deleteByIdIn(purchaseOrderDetailDeleteIds);

            return true;
        }

        if (purchaseOrder.getStatus() == PurchaseOrder.Status.CONFIRMED && purchaseOrderDto.getStatus() == PurchaseOrder.Status.RECEIVED) {
            purchaseOrder.setStatus(PurchaseOrder.Status.RECEIVED);
            purchaseOrder.setReceivedDate(DateUtils.getCurrentTime());

            List<ProductInventory> productInventories = purchaseOrderDto.getDetails().stream()
                    .map(purchaseOrderDetail -> {
                        ProductInventory productInventory = new ProductInventory();
                        productInventory.setType(ProductInventory.Type.PURCHASE_ORDER);
                        productInventory.setTransactionType(ProductInventory.TransactionType.ADD);
                        productInventory.setQuantity(purchaseOrderDetail.getQuantity());
                        productInventory.setProductId(purchaseOrderDetail.getProductId());
                        productInventory.setPurchaseOrderDetailId(purchaseOrderDetail.getId());
                        return productInventory;
                    })
                    .toList();
            productInventoryRepository.saveAllAndFlush(productInventories);

            List<ProductInventoryLocation> productInventoryLocations = purchaseOrderDto.getDetails().stream()
                    .flatMap(detail -> detail.getLocations().stream().map(location -> {
                        ProductInventoryLocation productInventoryLocation = new ProductInventoryLocation();
                        productInventoryLocation.setProductId(detail.getProductId());
                        productInventoryLocation.setPurchaseOrderDetailId(detail.getId());
                        productInventoryLocation.setLocationId(location.getId());
                        productInventoryLocation.setStatus(ProductInventoryLocation.Status.ACTIVE);
                        return productInventoryLocation;
                    }))
                    .toList();
            productInventoryLocationRepository.saveAllAndFlush(productInventoryLocations);
        }

        return false;
    }

    @Override
    public PurchaseOrderDto getPurchaseOrder(String purchaseOrderId) {
        PurchaseOrderDto purchaseOrderDto = new PurchaseOrderDto();
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new RuntimeException("Purchase order not found with id: " + purchaseOrderId));

        List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrderDetailRepository.findByPurchaseOrderId(purchaseOrderId);
        purchaseOrderDto = PurchaseOrderDto.toDto(purchaseOrder);
        purchaseOrderDto.setDetails(purchaseOrderDetails.stream().map(PurchaseOrderDetailDto::toDto).collect(Collectors.toList()));
        return purchaseOrderDto;
    }

    private String generatePurchaseOrderId(
            String supplierId,
            String warehouseId
    ) {
        return "PO"
                + "-" + getNextPurchaseOrderSeq()
//                + "-" + supplierId
//                + "-" + warehouseId
                + "-S"
                + "-W"
                + "-" + new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    @Override
    public Page<PurchaseOrder> getListPurchaseOrder(DataTableRequest dataTableRequest) {
        PageRequest pageable = dataTableRequest.toPageable();
        BaseSpecification<PurchaseOrder> specIdContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{PurchaseOrder.Fields.id})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        return purchaseOrderRepository.findAll(Specification.where(specIdContains), pageable);
    }

    @Override
    public ReferenceDataPurchaseOrderResponse getReferenceDataPurchaseOrder() {
        ReferenceDataPurchaseOrderResponse referenceDataPurchaseOrderResponse = new ReferenceDataPurchaseOrderResponse();
        referenceDataPurchaseOrderResponse.setWarehouses(
                warehouseRepository.findByStatus(Warehouse.Status.ACTIVE).stream()
                        .map(WarehouseDto::toDto)
                        .collect(Collectors.toList())
        );
        referenceDataPurchaseOrderResponse.setSuppliers(
                supplierRepository.findByStatus(Supplier.Status.ACTIVE).stream()
                        .map(SupplierDto::toDto)
                        .collect(Collectors.toList())
        );
        referenceDataPurchaseOrderResponse.setCategories(
                productCategoryRepository.findByStatus(ProductCategory.Status.ACTIVE).stream()
                        .map(ProductCategoryDto::toDto)
                        .collect(Collectors.toList())
        );
//        List<Zone> zones = zoneRepository.findByWarehouseIdAndStatus(warehouseId, Zone.Status.ACTIVE);
//        List<Shelf> shelves = shelfRepository.findByZoneIdInAndStatus(
//                zones.stream()
//                        .map(Zone::getId)
//                        .collect(Collectors.toList()),
//                Shelf.Status.ACTIVE
//        );
//        List<Floor> floors = floorRepository.findByShelfIdInAndStatus(
//                shelves.stream()
//                        .map(Shelf::getId)
//                        .collect(Collectors.toList()),
//                Floor.Status.ACTIVE
//        );
        return referenceDataPurchaseOrderResponse;
    }
}
