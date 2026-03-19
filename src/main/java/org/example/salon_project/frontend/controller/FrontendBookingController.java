package org.example.salon_project.frontend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.salon_project.frontend.dto.BookingRecordDto;
import org.example.salon_project.frontend.dto.BookingSaveRequest;
import org.example.salon_project.frontend.service.FrontendBookingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/frontend/bookings")
@RequiredArgsConstructor
public class FrontendBookingController {

    private final FrontendBookingService bookingService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<BookingRecordDto> listBookings() {
        return bookingService.listBookings();
    }

    @PostMapping
    public BookingRecordDto createBooking(@Valid @RequestBody BookingSaveRequest request, Authentication authentication) {
        return bookingService.createBooking(request, authentication != null ? authentication.getName() : null);
    }

    @PutMapping("/{id}")
    public BookingRecordDto updateBooking(@PathVariable String id, @Valid @RequestBody BookingSaveRequest request, Authentication authentication) {
        boolean admin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        return bookingService.saveBooking(id, request, authentication != null ? authentication.getName() : null, admin);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBooking(@PathVariable String id) {
        bookingService.deleteBooking(id);
    }
}
