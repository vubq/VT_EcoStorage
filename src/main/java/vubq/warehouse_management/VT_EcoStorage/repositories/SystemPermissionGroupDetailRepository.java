package vubq.warehouse_management.VT_EcoStorage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vubq.warehouse_management.VT_EcoStorage.entities.SystemPermissionGroupDetail;

import java.util.List;

@Repository
public interface SystemPermissionGroupDetailRepository extends JpaRepository<SystemPermissionGroupDetail, String> {

    List<SystemPermissionGroupDetail> findAllById_SystemPermissionGroupId(String permissionGroupId);

    void deleteBySystemPermissionGroupId(String systemPermissionGroupId);
}
