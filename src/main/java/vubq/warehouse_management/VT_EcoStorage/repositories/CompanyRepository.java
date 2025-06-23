package vubq.warehouse_management.VT_EcoStorage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vubq.warehouse_management.VT_EcoStorage.entities.Company;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {

    List<Company> findByStatus(Company.Status status);
}
