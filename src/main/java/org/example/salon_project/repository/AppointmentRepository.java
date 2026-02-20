package org.example.salon_project.repository;

import org.example.salon_project.domain.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("""
            SELECT a FROM Appointment a
            WHERE (:clientId IS NULL OR a.client.id = :clientId)
              AND (:masterId IS NULL OR a.master.id = :masterId)
              AND (:status IS NULL OR a.status = :status)
            """)
    Page<Appointment> findWithFilters(
            @Param("clientId") Long clientId,
            @Param("masterId") Long masterId,
            @Param("status") String status,
            Pageable pageable);

    boolean existsByScheduleId(Long scheduleId);
}
