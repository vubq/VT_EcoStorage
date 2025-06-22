package vubq.warehouse_management.VT_EcoStorage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vubq.warehouse_management.VT_EcoStorage.entities.Floor;

import java.util.List;

@Repository
public interface FloorRepository extends JpaRepository<Floor, String> {

    List<Floor> findByShelfIdInAndStatus(List<String> shelves, Floor.Status status);
}
