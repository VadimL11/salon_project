package org.example.salon_project.repository;

import org.example.salon_project.domain.SalonBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalonBookingRepository extends JpaRepository<SalonBooking, Long> {
    List<SalonBooking> findAllByOrderBySortOrderAsc();

    Optional<SalonBooking> findByExternalId(String externalId);

    @Query("""
            select count(b) > 0
            from SalonBooking b
            where b.master.id = :masterId
              and b.bookingDate = :bookingDate
              and b.bookingTime = :bookingTime
              and b.status <> 'cancelled'
              and (:excludedBookingId is null or b.id <> :excludedBookingId)
            """)
    boolean existsConflictingBooking(Long masterId, LocalDate bookingDate, LocalTime bookingTime, Long excludedBookingId);
}
