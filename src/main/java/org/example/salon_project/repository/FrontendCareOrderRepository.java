package org.example.salon_project.repository;

import org.example.salon_project.domain.FrontendCareOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FrontendCareOrderRepository extends JpaRepository<FrontendCareOrder, Long> {
    List<FrontendCareOrder> findAllByOrderByCreatedAtDesc();

    Optional<FrontendCareOrder> findByExternalId(String externalId);
}
