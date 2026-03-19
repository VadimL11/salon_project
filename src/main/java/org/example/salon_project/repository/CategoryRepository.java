package org.example.salon_project.repository;

import org.example.salon_project.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByExternalIdIsNotNullOrderBySortOrderAsc();

    Optional<Category> findByExternalId(String externalId);

    Optional<Category> findBySlug(String slug);
}
