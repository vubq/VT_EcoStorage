package vubq.warehouse_management.VT_EcoStorage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vubq.warehouse_management.VT_EcoStorage.entities.Shelf;

import java.util.List;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, String> {

    List<Shelf> findByZoneIdInAndStatus(List<String> zoneIds, Shelf.Status status);

    boolean existsByNameAndZoneId(String name, String zoneId);

    boolean existsByNameAndZoneIdAndIdNot(String name, String zoneId, String id);
}
