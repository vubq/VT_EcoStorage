package vubq.warehouse_management.VT_EcoStorage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vubq.warehouse_management.VT_EcoStorage.entities.UserPermissionGroup;

import java.util.List;

@Repository
public interface UserPermissionGroupRepository extends JpaRepository<UserPermissionGroup, String> {

    @Query("SELECT upg.id.systemPermissionGroupId FROM UserPermissionGroup upg WHERE upg.id.userId = :userId")
    List<String> findSystemPermissionGroupIdsByUserId(@Param("userId") String userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserPermissionGroup upg WHERE upg.id.userId = :userId")
    void deleteAllByUserId(@Param("userId") String userId);
}
