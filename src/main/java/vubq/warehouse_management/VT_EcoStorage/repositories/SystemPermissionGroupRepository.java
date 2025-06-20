package vubq.warehouse_management.VT_EcoStorage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vubq.warehouse_management.VT_EcoStorage.entities.SystemPermissionGroup;

import java.util.List;

@Repository
public interface SystemPermissionGroupRepository extends JpaRepository<SystemPermissionGroup, String> {

    List<SystemPermissionGroup> findAllByStatus(SystemPermissionGroup.Status status);
}
