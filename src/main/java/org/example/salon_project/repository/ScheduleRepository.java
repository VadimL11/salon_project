package org.example.salon_project.repository;

import org.example.salon_project.domain.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("""
            SELECT s FROM Schedule s
            WHERE (:masterId IS NULL OR s.master.id = :masterId)
              AND (:from IS NULL OR s.workDate >= :from)
              AND (:to IS NULL OR s.workDate <= :to)
              AND (:available IS NULL OR s.available = :available)
            """)
    Page<Schedule> findWithFilters(
            @Param("masterId") Long masterId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("available") Boolean available,
            Pageable pageable);
}
