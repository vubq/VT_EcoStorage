package vubq.warehouse_management.VT_EcoStorage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vubq.warehouse_management.VT_EcoStorage.entities.Warehouse;

import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, String> {

    List<Warehouse> findByStatus(Warehouse.Status status);

    List<Warehouse> findByCompanyIdAndStatus(String companyId, Warehouse.Status status);

    Warehouse findByIdAndStatus(String warehouseId, Warehouse.Status status);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, String id);
}
