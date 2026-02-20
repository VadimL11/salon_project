package org.example.salon_project.repository;

import org.example.salon_project.domain.BeautyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BeautyServiceRepository extends JpaRepository<BeautyService, Long> {
    Page<BeautyService> findByCategoryId(Long categoryId, Pageable pageable);

    Page<BeautyService> findByActive(boolean active, Pageable pageable);

    Page<BeautyService> findByCategoryIdAndActive(Long categoryId, boolean active, Pageable pageable);
}
