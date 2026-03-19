package org.example.salon_project.repository;

import org.example.salon_project.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByStockGreaterThan(int stock, Pageable pageable);

    List<Product> findByExternalIdIsNotNullOrderBySortOrderAsc();

    Optional<Product> findByExternalId(String externalId);
}
