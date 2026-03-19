package org.example.salon_project.frontend.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record FrontendBootstrapDto(
        List<ServiceCategoryDto> serviceCategories,
        List<ServiceItemDto> services,
        List<MasterDto> masters,
        List<BookingSlotDto> bookingSlots,
        List<CareProductDto> careProducts,
        List<DrinkItemDto> drinks,
        List<TrendDto> trends,
        List<BookingRecordDto> bookings,
        List<CareOrderRecordDto> careOrders,
        List<DrinkOrderRecordDto> drinkOrders) {
}
