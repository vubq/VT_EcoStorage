package vubq.warehouse_management.VT_EcoStorage.services.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vubq.warehouse_management.VT_EcoStorage.dtos.FloorDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.ShelfDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.WarehouseDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.ZoneDto;
import vubq.warehouse_management.VT_EcoStorage.entities.*;
import vubq.warehouse_management.VT_EcoStorage.repositories.*;
import vubq.warehouse_management.VT_EcoStorage.services.WarehouseService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

        List<Zone> zones = zoneRepository.findByWarehouseIdAndStatus(
                warehouseId,
                Zone.Status.ACTIVE
        );

        List<Shelf> shelves = shelfRepository.findByZoneIdInAndStatus(
                zones.stream().map(Zone::getId).collect(Collectors.toList()),
                Shelf.Status.ACTIVE
        );

        List<Floor> floors = floorRepository.findByShelfIdInAndStatus(
                shelves.stream().map(Shelf::getId).collect(Collectors.toList()),
                Floor.Status.ACTIVE
        );

        Map<String, List<FloorDto>> floorMap = floors.stream()
                .map(FloorDto::toDto)
                .collect(Collectors.groupingBy(FloorDto::getShelfId));

        Map<String, List<ShelfDto>> shelfMap = shelves.stream().map(shelf -> {
            ShelfDto shelfDto = ShelfDto.toDto(shelf);
            shelfDto.setFloors(floorMap.getOrDefault(shelf.getId(), new ArrayList<>()));
            return shelfDto;
        }).collect(Collectors.groupingBy(ShelfDto::getZoneId));

        List<ZoneDto> zoneDtos = zones.stream().map(zone -> {
            ZoneDto zoneDto = ZoneDto.toDto(zone);
            zoneDto.setShelves(shelfMap.getOrDefault(zone.getId(), new ArrayList<>()));
            return zoneDto;
        }).collect(Collectors.toList());

        WarehouseDto warehouseDto = WarehouseDto.toDto(warehouse);
        warehouseDto.setZones(zoneDtos);
        return warehouseDto;
    }
}
