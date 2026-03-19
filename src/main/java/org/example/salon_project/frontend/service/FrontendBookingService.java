package org.example.salon_project.frontend.service;

import lombok.RequiredArgsConstructor;
import org.example.salon_project.domain.BeautyService;
import org.example.salon_project.domain.Category;
import org.example.salon_project.domain.Client;
import org.example.salon_project.domain.Master;
import org.example.salon_project.domain.SalonBooking;
import org.example.salon_project.exception.ResourceNotFoundException;
import org.example.salon_project.frontend.dto.BookingRecordDto;
import org.example.salon_project.frontend.dto.BookingSaveRequest;
import org.example.salon_project.frontend.mapper.FrontendMapper;
import org.example.salon_project.repository.BeautyServiceRepository;
import org.example.salon_project.repository.CategoryRepository;
import org.example.salon_project.repository.ClientRepository;
import org.example.salon_project.repository.MasterRepository;
import org.example.salon_project.repository.SalonBookingRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FrontendBookingService {

    private static final Set<String> VALID_STATUSES = Set.of("new", "confirmed", "completed", "cancelled");

    private final SalonBookingRepository bookingRepository;
    private final CategoryRepository categoryRepository;
    private final BeautyServiceRepository beautyServiceRepository;
    private final MasterRepository masterRepository;
    private final ClientRepository clientRepository;
    private final FrontendMapper mapper;

    public List<BookingRecordDto> listBookings() {
        return bookingRepository.findAllByOrderBySortOrderAsc().stream().map(mapper::toDto).toList();
    }

    @Transactional
    public BookingRecordDto createBooking(BookingSaveRequest request, String authenticatedExternalId) {
        SalonBooking booking = new SalonBooking();
        booking.setExternalId(defaultId(request.id()));
        booking.setSortOrder(nextSortOrder());
        applyRequest(booking, request, authenticatedExternalId, false);
        return mapper.toDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingRecordDto saveBooking(String externalId, BookingSaveRequest request, String authenticatedExternalId, boolean admin) {
        SalonBooking booking = bookingRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", externalId));
        Client authenticatedClient = resolveAuthenticatedClient(authenticatedExternalId);

        if (!admin) {
            if (authenticatedClient == null) {
                throw new AccessDeniedException("Authentication required");
            }
            if (!belongsToClient(booking, authenticatedClient)) {
                throw new AccessDeniedException("You can only manage your own bookings");
            }
            if ("completed".equals(booking.getStatus()) || "cancelled".equals(booking.getStatus())) {
                throw new IllegalArgumentException("Only active bookings can be changed");
            }

            String requestedStatus = normalizeStatus(request.status(), true);
            if (!requestedStatus.equals(booking.getStatus()) && !"cancelled".equals(requestedStatus)) {
                throw new IllegalArgumentException("Clients can only cancel their bookings");
            }
        }

        applyRequest(booking, request, authenticatedExternalId, true);
        return mapper.toDto(bookingRepository.save(booking));
    }

    @Transactional
    public void deleteBooking(String externalId) {
        bookingRepository.delete(bookingRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", externalId)));
    }

    private void applyRequest(SalonBooking booking, BookingSaveRequest request, String authenticatedExternalId, boolean update) {
        var bookingDate = parseDate(request.date());
        var bookingTime = parseTime(request.time());
        String status = normalizeStatus(request.status(), update);

        Category category = resolveCategory(request.categoryId());
        BeautyService service = resolveService(request.serviceId());
        if (service != null) {
            if (category == null) {
                category = service.getCategory();
            } else if (!service.getCategory().getId().equals(category.getId())) {
                throw new IllegalArgumentException("serviceId does not belong to categoryId");
            }
        }

        Master master = resolveMaster(request.masterId());
        if (master != null && category != null && !master.getSpecialtyCategories().isEmpty()) {
            Category finalCategory = category;
            boolean matches = master.getSpecialtyCategories().stream()
                    .anyMatch(entry -> entry.getId().equals(finalCategory.getId()));
            if (!matches) {
                throw new IllegalArgumentException("masterId is not available for categoryId");
            }
        }

        if (master != null && bookingRepository.existsConflictingBooking(master.getId(), bookingDate, bookingTime, booking.getId())) {
            throw new IllegalArgumentException("The selected master already has a booking at this date and time");
        }

        Client client = resolveAuthenticatedClient(authenticatedExternalId);
        if (client == null && request.customer().email() != null && !request.customer().email().isBlank()) {
            client = clientRepository.findByEmailIgnoreCase(request.customer().email().trim()).orElse(null);
        }

        booking.setClient(client);
        booking.setCategory(category);
        booking.setService(service);
        booking.setMaster(master);
        booking.setCustomerFirstName(request.customer().firstName().trim());
        booking.setCustomerLastName(request.customer().lastName().trim());
        booking.setCustomerPhone(request.customer().phone().trim());
        booking.setCustomerEmail(blankToNull(request.customer().email()));
        booking.setBookingDate(bookingDate);
        booking.setBookingTime(bookingTime);
        booking.setStatus(status);
        booking.setNote(request.note() == null ? "" : request.note().trim());

        if (!update && request.createdAt() != null && !request.createdAt().isBlank()) {
            booking.setCreatedAt(OffsetDateTime.parse(request.createdAt()));
        }
    }

    private Category resolveCategory(String externalId) {
        if (externalId == null || externalId.isBlank()) {
            return null;
        }
        return categoryRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", externalId));
    }

    private BeautyService resolveService(String externalId) {
        if (externalId == null || externalId.isBlank()) {
            return null;
        }
        return beautyServiceRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Service", externalId));
    }

    private Master resolveMaster(String externalId) {
        if (externalId == null || externalId.isBlank()) {
            return null;
        }
        return masterRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Master", externalId));
    }

    private Client resolveAuthenticatedClient(String externalId) {
        if (externalId == null || externalId.isBlank()) {
            return null;
        }
        return clientRepository.findByExternalId(externalId).orElse(null);
    }

    private boolean belongsToClient(SalonBooking booking, Client client) {
        if (booking.getClient() != null && client.getExternalId().equals(booking.getClient().getExternalId())) {
            return true;
        }
        if (equalsNormalizedEmail(booking.getCustomerEmail(), client.getEmail())) {
            return true;
        }
        return equalsNormalizedPhone(booking.getCustomerPhone(), client.getPhone());
    }

    private java.time.LocalDate parseDate(String value) {
        try {
            return mapper.parseDate(value);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("date must use YYYY-MM-DD format");
        }
    }

    private java.time.LocalTime parseTime(String value) {
        try {
            return mapper.parseTime(value);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("time must use HH:mm format");
        }
    }

    private String normalizeStatus(String status, boolean update) {
        String normalized = status == null || status.isBlank()
                ? (update ? null : "new")
                : status.trim().toLowerCase();

        if (normalized == null) {
            normalized = "new";
        }

        if (!VALID_STATUSES.contains(normalized)) {
            throw new IllegalArgumentException("status must be one of: new, confirmed, completed, cancelled");
        }
        return normalized;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private boolean equalsNormalizedEmail(String left, String right) {
        String normalizedLeft = normalizeEmail(left);
        String normalizedRight = normalizeEmail(right);
        return !normalizedLeft.isBlank() && normalizedLeft.equals(normalizedRight);
    }

    private boolean equalsNormalizedPhone(String left, String right) {
        String normalizedLeft = normalizePhone(left);
        String normalizedRight = normalizePhone(right);
        return !normalizedLeft.isBlank() && normalizedLeft.equals(normalizedRight);
    }

    private String normalizeEmail(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizePhone(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }

    private String defaultId(String requestedId) {
        return requestedId != null && !requestedId.isBlank() ? requestedId : "booking-" + UUID.randomUUID();
    }

    private Integer nextSortOrder() {
        List<Integer> sortOrders = bookingRepository.findAllByOrderBySortOrderAsc().stream()
                .map(SalonBooking::getSortOrder)
                .toList();
        if (sortOrders.isEmpty()) {
            return 0;
        }
        return sortOrders.getFirst() - 1;
    }
}
