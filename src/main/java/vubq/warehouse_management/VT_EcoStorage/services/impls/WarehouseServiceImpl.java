package vubq.warehouse_management.VT_EcoStorage.services.impls;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vubq.warehouse_management.VT_EcoStorage.dtos.FloorDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.ShelfDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.WarehouseDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.ZoneDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.requests.MoveLocationRequest;
import vubq.warehouse_management.VT_EcoStorage.entities.*;
import vubq.warehouse_management.VT_EcoStorage.repositories.*;
import vubq.warehouse_management.VT_EcoStorage.services.WarehouseService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    final private CompanyRepository companyRepository;
    final private WarehouseRepository warehouseRepository;
    final private ZoneRepository zoneRepository;
    final private ShelfRepository shelfRepository;
    final private FloorRepository floorRepository;
    final private ProductInventoryLocationRepository productInventoryLocationRepository;
    final private ProductInventoryRepository productInventoryRepository;
    final private ProductInventoryLocationHistoryRepository productInventoryLocationHistoryRepository;

    @Override
    public List<WarehouseDto> getWarehouses() {
        List<Company> companies = companyRepository.findByStatus(Company.Status.ACTIVE);
        List<Warehouse> warehouses = warehouseRepository.findByCompanyIdAndStatus(companies.get(0).getId(), Warehouse.Status.ACTIVE);
        List<Zone> zones = zoneRepository.findByWarehouseIdInAndStatus(warehouses.stream()
                        .map(Warehouse::getId)
                        .collect(Collectors.toList()),
                Zone.Status.ACTIVE);
        List<Shelf> shelves = shelfRepository.findByZoneIdInAndStatus(
                zones.stream()
                        .map(Zone::getId)
                        .collect(Collectors.toList()),
                Shelf.Status.ACTIVE
        );
        List<Floor> floors = floorRepository.findByShelfIdInAndStatus(
                shelves.stream()
                        .map(Shelf::getId)
                        .collect(Collectors.toList()),
                Floor.Status.ACTIVE
        );

        Map<String, List<FloorDto>> floorMap = floors.stream()
                .map(FloorDto::toDto)
                .collect(Collectors.groupingBy(FloorDto::getShelfId));

        Map<String, List<ShelfDto>> shelfMap = shelves.stream().map(shelf -> {
            ShelfDto shelfDto = ShelfDto.toDto(shelf);
            List<FloorDto> listFloor = floorMap.getOrDefault(shelf.getId(), new ArrayList<>());
            shelfDto.setFloors(listFloor);
            return shelfDto;
        }).collect(Collectors.groupingBy(ShelfDto::getZoneId));

        Map<String, List<ZoneDto>> zoneMap = zones.stream().map(zone -> {
            ZoneDto zoneDto = ZoneDto.toDto(zone);
            List<ShelfDto> listShelf = shelfMap.getOrDefault(zone.getId(), new ArrayList<>());
            zoneDto.setShelves(listShelf);
            return zoneDto;
        }).collect(Collectors.groupingBy(ZoneDto::getWarehouseId));

        return warehouses.stream().map(warehouse -> {
            WarehouseDto warehouseDto = WarehouseDto.toDto(warehouse);
            List<ZoneDto> listZones = zoneMap.getOrDefault(warehouse.getId(), new ArrayList<>());
            warehouseDto.setZones(listZones);
            return warehouseDto;
        }).toList();
    }

    @Override
    public ZoneDto createOrUpdateZone(ZoneDto zoneDto) {
        Zone zone = new Zone();
        zone.setName(zoneDto.getName());
        zone.setStatus(zoneDto.getStatus());
        zone.setWarehouseId(zoneDto.getWarehouseId());
        zoneRepository.save(zone);
        return ZoneDto.toDto(zone);
    }

    @Override
    public ShelfDto createOrUpdateShelf(ShelfDto shelfDto) {
        Shelf shelf = new Shelf();
        shelf.setName(shelfDto.getName());
        shelf.setStatus(shelfDto.getStatus());
        shelf.setZoneId(shelfDto.getZoneId());
        shelfRepository.save(shelf);
        return ShelfDto.toDto(shelf);
    }

    @Override
    public List<FloorDto> createOrUpdateFloor(FloorDto floorDto) {
        Long floorMax = floorRepository.findMaxFloorByShelfId(floorDto.getShelfId());
        if (floorMax == null) {
            floorMax = 0L;
        }

        List<Floor> floors = new ArrayList<>();
        for (int i = 0; i < floorDto.getQuantity(); i++) {
            Floor floor = new Floor();
            floor.setName(floorDto.getName());
            floor.setStatus(floorDto.getStatus());
            floor.setShelfId(floorDto.getShelfId());
            floor.setFloor(++floorMax);
            floors.add(floor);
        }

        floors = this.floorRepository.saveAllAndFlush(floors);
        return floors.stream().map(FloorDto::toDto).collect(Collectors.toList());
    }

    @Override
    public WarehouseDto getWarehouse(String warehouseId) {
        Warehouse warehouse = warehouseRepository.findByIdAndStatus(warehouseId, Warehouse.Status.ACTIVE);
        if (warehouse == null) {
            return null;
        }

        List<Zone> zones = zoneRepository.findByWarehouseIdAndStatus(warehouseId, Zone.Status.ACTIVE);
        List<Shelf> shelves = shelfRepository.findByZoneIdInAndStatus(
                zones.stream()
                        .map(Zone::getId)
                        .collect(Collectors.toList()),
                Shelf.Status.ACTIVE
        );
        List<Floor> floors = floorRepository.findByShelfIdInAndStatus(
                shelves.stream()
                        .map(Shelf::getId)
                        .collect(Collectors.toList()),
                Floor.Status.ACTIVE
        );

        Map<String, List<FloorDto>> floorMap = floors.stream()
                .map(FloorDto::toDto)
                .collect(Collectors.groupingBy(FloorDto::getShelfId));

        Map<String, List<ShelfDto>> shelfMap = shelves.stream().map(shelf -> {
            ShelfDto shelfDto = ShelfDto.toDto(shelf);
            List<FloorDto> listFloor = floorMap.getOrDefault(shelf.getId(), new ArrayList<>());
            shelfDto.setFloors(listFloor);
            return shelfDto;
        }).collect(Collectors.groupingBy(ShelfDto::getZoneId));

        Map<String, List<ZoneDto>> zoneMap = zones.stream().map(zone -> {
            ZoneDto zoneDto = ZoneDto.toDto(zone);
            List<ShelfDto> listShelf = shelfMap.getOrDefault(zone.getId(), new ArrayList<>());
            zoneDto.setShelves(listShelf);
            return zoneDto;
        }).collect(Collectors.groupingBy(ZoneDto::getWarehouseId));

        WarehouseDto warehouseDto = WarehouseDto.toDto(warehouse);
        List<ZoneDto> listZones = zoneMap.getOrDefault(warehouse.getId(), new ArrayList<>());
        warehouseDto.setZones(listZones);
        return warehouseDto;
    }

    @Override
    public WarehouseDto createOrUpdateWarehouse(WarehouseDto warehouseDto) {
        List<Company> companies = companyRepository.findByStatus(Company.Status.ACTIVE);
        if (companies.isEmpty()) {
            throw new RuntimeException("Chua co thong tin cong ty");
        }

        Warehouse warehouse;
        if (StringUtils.isEmpty(warehouseDto.getId())) {
            warehouse = new Warehouse();
            warehouse.setStatus(Warehouse.Status.ACTIVE);
        } else {
            warehouse = warehouseRepository.findById(warehouseDto.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy kho có ID: " + warehouseDto.getId()));
            warehouse.setStatus(warehouseDto.getStatus());
        }
        warehouse.setName(warehouseDto.getName());
        warehouse.setAddress(warehouseDto.getAddress());
        warehouse.setStatus(warehouseDto.getStatus());
        warehouse.setCompanyId(companies.get(0).getId());
        warehouseRepository.save(warehouse);

        if (!StringUtils.isEmpty(warehouseDto.getId())) {
            List<Zone> zones = zoneRepository.findByWarehouseIdAndStatus(warehouseDto.getId(), Zone.Status.ACTIVE);
            List<Shelf> shelves = shelfRepository.findByZoneIdInAndStatus(
                    zones.stream()
                            .map(Zone::getId)
                            .collect(Collectors.toList()),
                    Shelf.Status.ACTIVE
            );
            List<Floor> floors = floorRepository.findByShelfIdInAndStatus(
                    shelves.stream()
                            .map(Shelf::getId)
                            .collect(Collectors.toList()),
                    Floor.Status.ACTIVE
            );

            Map<String, List<FloorDto>> floorMap = floors.stream()
                    .map(FloorDto::toDto)
                    .collect(Collectors.groupingBy(FloorDto::getShelfId));

            Map<String, List<ShelfDto>> shelfMap = shelves.stream().map(shelf -> {
                ShelfDto shelfDto = ShelfDto.toDto(shelf);
                List<FloorDto> listFloor = floorMap.getOrDefault(shelf.getId(), new ArrayList<>());
                shelfDto.setFloors(listFloor);
                return shelfDto;
            }).collect(Collectors.groupingBy(ShelfDto::getZoneId));

            Map<String, List<ZoneDto>> zoneMap = zones.stream().map(zone -> {
                ZoneDto zoneDto = ZoneDto.toDto(zone);
                List<ShelfDto> listShelf = shelfMap.getOrDefault(zone.getId(), new ArrayList<>());
                zoneDto.setShelves(listShelf);
                return zoneDto;
            }).collect(Collectors.groupingBy(ZoneDto::getWarehouseId));
            WarehouseDto warehouseDtoUpdate = WarehouseDto.toDto(warehouse);
            List<ZoneDto> listZones = zoneMap.getOrDefault(warehouse.getId(), new ArrayList<>());
            warehouseDtoUpdate.setZones(listZones);
            return warehouseDtoUpdate;
        }
        return WarehouseDto.toDto(warehouse);
    }

    @Override
    public boolean moveLocation(MoveLocationRequest moveLocationRequest) {
        String sourceLocationId = moveLocationRequest.getLocationId();
        List<MoveLocationRequest.LocationNew> targets = moveLocationRequest.getLocationsNew();

        if (targets.isEmpty()) throw new IllegalArgumentException("Không có vị trí đích");

        String productId = moveLocationRequest.getProductId();
        List<String> allLocationIds = new ArrayList<>(targets.stream()
                .map(MoveLocationRequest.LocationNew::getLocationId)
                .toList());
        allLocationIds.add(sourceLocationId);

        // Lấy tất cả ProductInventoryLocation liên quan
        List<ProductInventoryLocation> pilList =
                productInventoryLocationRepository.findByProductIdAndLocationIdIn(productId, allLocationIds);

        Map<String, ProductInventoryLocation> pilMap = pilList.stream().collect(Collectors.toMap(
                ProductInventoryLocation::getLocationId,
                pil -> pil
        ));

        ProductInventoryLocation sourcePIL = pilMap.get(sourceLocationId);
        if (sourcePIL == null) {
            throw new IllegalArgumentException("Không tìm thấy vị trí nguồn");
        }

        long totalMove = targets.stream().mapToLong(MoveLocationRequest.LocationNew::getQuantity).sum();
        if (sourcePIL.getInventoryQuantity() < totalMove) {
            throw new IllegalArgumentException("Tồn kho không đủ để chuyển");
        }

        // Trừ tồn kho vị trí nguồn
        sourcePIL.setInventoryQuantity(sourcePIL.getInventoryQuantity() - totalMove);

        List<ProductInventoryLocation> toSave = new ArrayList<>();
        List<ProductInventoryLocationHistory> histories = new ArrayList<>();

        for (MoveLocationRequest.LocationNew target : targets) {
            ProductInventoryLocation targetPIL = pilMap.get(target.getLocationId());

            if (targetPIL == null) {
                targetPIL = new ProductInventoryLocation();
                targetPIL.setProductId(target.getProductId());
                targetPIL.setLocationId(target.getLocationId());
                targetPIL.setInventoryQuantity(0L);
                targetPIL.setStatus(ProductInventoryLocation.Status.ACTIVE);
                pilMap.put(target.getLocationId(), targetPIL);
            }

            targetPIL.setInventoryQuantity(targetPIL.getInventoryQuantity() + target.getQuantity());
            toSave.add(targetPIL);
        }

        toSave.add(sourcePIL);
        List<ProductInventoryLocation> saved = productInventoryLocationRepository.saveAllAndFlush(toSave);

        Map<String, ProductInventoryLocation> savedMap = saved.stream().collect(Collectors.toMap(
                ProductInventoryLocation::getLocationId,
                Function.identity()
        ));

        for (MoveLocationRequest.LocationNew target : targets) {
            ProductInventoryLocation pil = savedMap.get(target.getLocationId());
            ProductInventoryLocationHistory h = new ProductInventoryLocationHistory();
            h.setProductInventoryLocationId(pil.getId());
            h.setType(ProductInventoryLocationHistory.Type.MOVE);
            h.setQuantity(target.getQuantity());
            histories.add(h);
        }

        productInventoryLocationHistoryRepository.saveAll(histories);

        List<ProductInventory> moveInventories = targets.stream().map(target -> {
            ProductInventory pi = new ProductInventory();
            pi.setProductId(target.getProductId());
            pi.setTransactionType(ProductInventory.TransactionType.MOVE);
            pi.setType(ProductInventory.Type.MOVE_LOCATION);
            pi.setQuantity(target.getQuantity());
            pi.setProductId(target.getProductId());
            pi.setNote("Di chuyển từ vị trí " + sourceLocationId + " đến " + target.getLocationId());
            return pi;
        }).collect(Collectors.toList());

        productInventoryRepository.saveAll(moveInventories);
        return true;
    }
}
