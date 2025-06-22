package vubq.warehouse_management.VT_EcoStorage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vubq.warehouse_management.VT_EcoStorage.entities.Zone;

import java.util.List;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, String> {

    List<Zone> findByWarehouseIdAndStatus(String warehouseId, Zone.Status status);
}
