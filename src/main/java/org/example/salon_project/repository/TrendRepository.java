package org.example.salon_project.repository;

import org.example.salon_project.domain.Trend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrendRepository extends JpaRepository<Trend, Long> {
    List<Trend> findAllByOrderBySortOrderAsc();

    Optional<Trend> findByExternalId(String externalId);
}
