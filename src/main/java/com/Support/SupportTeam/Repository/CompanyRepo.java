package com.Support.SupportTeam.Repository;

import com.Support.SupportTeam.Entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CompanyRepo extends JpaRepository<Company,Long> {
    Optional<Company> findByName(String companyName);

    boolean existsByName(String name);
}
