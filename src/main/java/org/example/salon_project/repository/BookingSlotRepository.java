package org.example.salon_project.repository;

import org.example.salon_project.domain.BookingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingSlotRepository extends JpaRepository<BookingSlot, Long> {
    List<BookingSlot> findAllByOrderBySortOrderAsc();

    Optional<BookingSlot> findByExternalId(String externalId);
}
