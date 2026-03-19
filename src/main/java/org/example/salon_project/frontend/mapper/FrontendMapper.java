package org.example.salon_project.frontend.mapper;

import org.example.salon_project.domain.BeautyService;
import org.example.salon_project.domain.BookingSlot;
import org.example.salon_project.domain.Category;
import org.example.salon_project.domain.Client;
import org.example.salon_project.domain.Drink;
import org.example.salon_project.domain.FrontendCareOrder;
import org.example.salon_project.domain.FrontendCareOrderItem;
import org.example.salon_project.domain.FrontendDrinkOrder;
import org.example.salon_project.domain.LocalizedTextEmbeddable;
import org.example.salon_project.domain.Master;
import org.example.salon_project.domain.MasterCredential;
import org.example.salon_project.domain.Product;
import org.example.salon_project.domain.SalonBooking;
import org.example.salon_project.domain.Trend;
import org.example.salon_project.frontend.dto.BookingCustomerDto;
import org.example.salon_project.frontend.dto.BookingRecordDto;
import org.example.salon_project.frontend.dto.BookingSlotDto;
import org.example.salon_project.frontend.dto.CareOrderRecordDto;
import org.example.salon_project.frontend.dto.CareProductDto;
import org.example.salon_project.frontend.dto.CartItemDto;
import org.example.salon_project.frontend.dto.DrinkItemDto;
import org.example.salon_project.frontend.dto.DrinkOrderRecordDto;
import org.example.salon_project.frontend.dto.FrontendUserDto;
import org.example.salon_project.frontend.dto.LocalizedTextDto;
import org.example.salon_project.frontend.dto.MasterDto;
import org.example.salon_project.frontend.dto.MasterCredentialDto;
import org.example.salon_project.frontend.dto.ServiceCategoryDto;
import org.example.salon_project.frontend.dto.ServiceItemDto;
import org.example.salon_project.frontend.dto.TrendDto;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Component
public class FrontendMapper {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public LocalizedTextDto toDto(LocalizedTextEmbeddable value) {
        if (value == null) {
            return new LocalizedTextDto("", "", "");
        }
        return new LocalizedTextDto(value.getUa(), value.getDe(), value.getGb());
    }

    public LocalizedTextEmbeddable toEmbeddable(LocalizedTextDto value) {
        if (value == null) {
            return LocalizedTextEmbeddable.builder().build();
        }
        return LocalizedTextEmbeddable.builder()
                .ua(defaultString(value.UA()))
                .de(defaultString(value.DE()))
                .gb(defaultString(value.GB()))
                .build();
    }

    public FrontendUserDto toUserDto(Client client) {
        return new FrontendUserDto(
                client.getFirstName(),
                client.getLastName(),
                client.getPhone(),
                client.getEmail(),
                client.getRole());
    }

    public ServiceCategoryDto toDto(Category category) {
        return new ServiceCategoryDto(
                category.getExternalId(),
                category.getSlug(),
                category.getIcon(),
                toDto(category.getTitle()),
                toDto(category.getLocalizedDescription()));
    }

    public ServiceItemDto toDto(BeautyService service) {
        return new ServiceItemDto(
                service.getExternalId(),
                service.getCategory() != null ? service.getCategory().getExternalId() : null,
                toDto(service.getTitle()),
                service.getDurationMinutes(),
                service.getPrice());
    }

    public MasterDto toDto(Master master) {
        List<String> specialtyCategoryIds = master.getSpecialtyCategories().stream()
                .sorted(Comparator.comparing(Category::getSortOrder))
                .map(Category::getExternalId)
                .toList();
        List<MasterCredentialDto> credentials = master.getCredentials().stream()
                .sorted(Comparator.comparing(MasterCredential::getSortOrder))
                .map(this::toDto)
                .toList();
        return new MasterDto(
                master.getExternalId(),
                displayName(master),
                toDto(master.getRole()),
                master.getInitials(),
                master.getExperienceLabel(),
                specialtyCategoryIds,
                credentials);
    }

    public MasterCredentialDto toDto(MasterCredential credential) {
        return new MasterCredentialDto(
                credential.getExternalId(),
                credential.getName(),
                credential.getType(),
                credential.getFileUrl());
    }

    public BookingSlotDto toDto(BookingSlot slot) {
        return new BookingSlotDto(slot.getExternalId(), slot.getPeriod(), formatTime(slot.getTime()));
    }

    public CareProductDto toDto(Product product) {
        return new CareProductDto(
                product.getExternalId(),
                toDto(product.getTitle()),
                product.getBrand(),
                product.getPrice(),
                product.getIcon());
    }

    public DrinkItemDto toDto(Drink drink) {
        return new DrinkItemDto(drink.getExternalId(), toDto(drink.getTitle()), drink.getIcon());
    }

    public TrendDto toDto(Trend trend) {
        return new TrendDto(
                trend.getExternalId(),
                toDto(trend.getTitle()),
                toDto(trend.getDescription()),
                trend.getGradient(),
                trend.getEmoji(),
                trend.getImageUrl());
    }

    public BookingRecordDto toDto(SalonBooking booking) {
        return new BookingRecordDto(
                booking.getExternalId(),
                new BookingCustomerDto(
                        booking.getCustomerFirstName(),
                        booking.getCustomerLastName(),
                        booking.getCustomerPhone(),
                        booking.getCustomerEmail()),
                booking.getCategory() != null ? booking.getCategory().getExternalId() : null,
                booking.getService() != null ? booking.getService().getExternalId() : null,
                booking.getMaster() != null ? booking.getMaster().getExternalId() : null,
                booking.getBookingDate().toString(),
                formatTime(booking.getBookingTime()),
                booking.getStatus(),
                booking.getNote(),
                booking.getCreatedAt().toString());
    }

    public CareOrderRecordDto toDto(FrontendCareOrder order) {
        List<CartItemDto> items = order.getItems().stream()
                .sorted(Comparator.comparing(FrontendCareOrderItem::getSortOrder))
                .map(this::toDto)
                .toList();
        return new CareOrderRecordDto(
                order.getExternalId(),
                order.getCustomerEmail(),
                order.getClient() != null ? order.getClient().getFirstName() : null,
                order.getClient() != null ? order.getClient().getLastName() : null,
                order.getClient() != null ? order.getClient().getPhone() : null,
                items,
                order.getTotalAmount(),
                order.getCreatedAt().toString());
    }

    public CartItemDto toDto(FrontendCareOrderItem item) {
        return new CartItemDto(
                item.getProductExternalId(),
                item.getItemName(),
                item.getUnitPrice(),
                item.getQuantity());
    }

    public DrinkOrderRecordDto toDto(FrontendDrinkOrder order) {
        return new DrinkOrderRecordDto(
                order.getExternalId(),
                order.getCustomerEmail(),
                order.getDrinkExternalId(),
                order.getCreatedAt().toString());
    }

    public LocalTime parseTime(String value) {
        return LocalTime.parse(value, TIME_FORMATTER);
    }

    public LocalDate parseDate(String value) {
        return LocalDate.parse(value);
    }

    public String formatTime(LocalTime value) {
        return value.format(TIME_FORMATTER);
    }

    private String displayName(Master master) {
        if (StringUtils.hasText(master.getDisplayName())) {
            return master.getDisplayName();
        }
        return (defaultString(master.getFirstName()) + " " + defaultString(master.getLastName())).trim();
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }
}
