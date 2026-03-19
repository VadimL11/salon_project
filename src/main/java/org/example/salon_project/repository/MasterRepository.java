package org.example.salon_project.repository;

import org.example.salon_project.domain.Master;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasterRepository extends JpaRepository<Master, Long> {
    Page<Master> findByActive(boolean active, Pageable pageable);

    List<Master> findByExternalIdIsNotNullOrderBySortOrderAsc();

    Optional<Master> findByExternalId(String externalId);
}
