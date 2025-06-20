package vubq.warehouse_management.VT_EcoStorage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vubq.warehouse_management.VT_EcoStorage.entities.SystemModule;

import java.util.List;

@Repository
public interface SystemModuleRepository extends JpaRepository<SystemModule, String> {

    List<SystemModule> findAllByStatus(SystemModule.Status status);
}
