package vubq.warehouse_management.VT_EcoStorage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vubq.warehouse_management.VT_EcoStorage.entities.UserPermission;

import java.util.List;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, String> {

    void deleteByUserId(String userId);

    List<UserPermission> findAllById_UserId(String userId);
}
