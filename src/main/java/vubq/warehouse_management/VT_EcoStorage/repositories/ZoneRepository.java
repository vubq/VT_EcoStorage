package vubq.warehouse_management.VT_EcoStorage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vubq.warehouse_management.VT_EcoStorage.entities.Zone;

import java.util.List;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, String> {

    List<Zone> findByWarehouseIdAndStatus(String warehouseId, Zone.Status status);

    List<Zone> findByWarehouseIdInAndStatus(List<String> warehouseIds, Zone.Status status);

    boolean existsByNameAndWarehouseId(String name, String warehouseId);

    boolean existsByNameAndWarehouseIdAndIdNot(String name, String warehouseId, String id);
}
