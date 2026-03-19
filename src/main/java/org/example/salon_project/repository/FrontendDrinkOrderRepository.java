package org.example.salon_project.repository;

import org.example.salon_project.domain.FrontendDrinkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FrontendDrinkOrderRepository extends JpaRepository<FrontendDrinkOrder, Long> {
    List<FrontendDrinkOrder> findAllByOrderByCreatedAtDesc();

    Optional<FrontendDrinkOrder> findByExternalId(String externalId);
}
