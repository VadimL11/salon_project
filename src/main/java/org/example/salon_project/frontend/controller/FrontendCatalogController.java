package org.example.salon_project.frontend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.salon_project.frontend.dto.BookingSlotDto;
import org.example.salon_project.frontend.dto.BookingSlotSaveRequest;
import org.example.salon_project.frontend.dto.CareProductDto;
import org.example.salon_project.frontend.dto.CareProductSaveRequest;
import org.example.salon_project.frontend.dto.DrinkItemDto;
import org.example.salon_project.frontend.dto.DrinkItemSaveRequest;
import org.example.salon_project.frontend.dto.FrontendBootstrapDto;
import org.example.salon_project.frontend.dto.MasterDto;
import org.example.salon_project.frontend.dto.MasterSaveRequest;
import org.example.salon_project.frontend.dto.ServiceCategoryDto;
import org.example.salon_project.frontend.dto.ServiceCategorySaveRequest;
import org.example.salon_project.frontend.dto.ServiceItemDto;
import org.example.salon_project.frontend.dto.ServiceItemSaveRequest;
import org.example.salon_project.frontend.dto.TrendDto;
import org.example.salon_project.frontend.dto.TrendSaveRequest;
import org.example.salon_project.frontend.service.FrontendCatalogService;
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
@RequestMapping("/api/v1/frontend")
@RequiredArgsConstructor
public class FrontendCatalogController {

    private final FrontendCatalogService catalogService;

    @GetMapping("/bootstrap")
    public FrontendBootstrapDto bootstrap(Authentication authentication) {
        boolean admin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        return catalogService.bootstrap(authentication != null ? authentication.getName() : null, admin);
    }

    @GetMapping("/service-categories")
    public List<ServiceCategoryDto> listCategories() {
        return catalogService.listCategories();
    }

    @PostMapping("/service-categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ServiceCategoryDto createCategory(@Valid @RequestBody ServiceCategorySaveRequest request) {
        return catalogService.saveCategory(request);
    }

    @PutMapping("/service-categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ServiceCategoryDto updateCategory(@PathVariable String id, @Valid @RequestBody ServiceCategorySaveRequest request) {
        return catalogService.saveCategory(new ServiceCategorySaveRequest(id, request.slug(), request.icon(), request.title(), request.description()));
    }

    @DeleteMapping("/service-categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(@PathVariable String id) {
        catalogService.deleteCategory(id);
    }

    @GetMapping("/services")
    public List<ServiceItemDto> listServices() {
        return catalogService.listServices();
    }

    @PostMapping("/services")
    @PreAuthorize("hasRole('ADMIN')")
    public ServiceItemDto createService(@Valid @RequestBody ServiceItemSaveRequest request) {
        return catalogService.saveService(request);
    }

    @PutMapping("/services/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ServiceItemDto updateService(@PathVariable String id, @Valid @RequestBody ServiceItemSaveRequest request) {
        return catalogService.saveService(new ServiceItemSaveRequest(id, request.categoryId(), request.title(), request.durationMinutes(), request.price()));
    }

    @DeleteMapping("/services/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteService(@PathVariable String id) {
        catalogService.deleteService(id);
    }

    @GetMapping("/masters")
    public List<MasterDto> listMasters() {
        return catalogService.listMasters();
    }

    @PostMapping("/masters")
    @PreAuthorize("hasRole('ADMIN')")
    public MasterDto createMaster(@Valid @RequestBody MasterSaveRequest request) {
        return catalogService.saveMaster(request);
    }

    @PutMapping("/masters/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public MasterDto updateMaster(@PathVariable String id, @Valid @RequestBody MasterSaveRequest request) {
        return catalogService.saveMaster(new MasterSaveRequest(
                id,
                request.name(),
                request.role(),
                request.initials(),
                request.experienceLabel(),
                request.specialtyCategoryIds(),
                request.credentials()));
    }

    @DeleteMapping("/masters/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteMaster(@PathVariable String id) {
        catalogService.deleteMaster(id);
    }

    @GetMapping("/booking-slots")
    public List<BookingSlotDto> listBookingSlots() {
        return catalogService.listBookingSlots();
    }

    @PostMapping("/booking-slots")
    @PreAuthorize("hasRole('ADMIN')")
    public BookingSlotDto createBookingSlot(@Valid @RequestBody BookingSlotSaveRequest request) {
        return catalogService.saveBookingSlot(request);
    }

    @PutMapping("/booking-slots/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BookingSlotDto updateBookingSlot(@PathVariable String id, @Valid @RequestBody BookingSlotSaveRequest request) {
        return catalogService.saveBookingSlot(new BookingSlotSaveRequest(id, request.period(), request.time()));
    }

    @DeleteMapping("/booking-slots/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBookingSlot(@PathVariable String id) {
        catalogService.deleteBookingSlot(id);
    }

    @GetMapping("/care-products")
    public List<CareProductDto> listCareProducts() {
        return catalogService.listCareProducts();
    }

    @PostMapping("/care-products")
    @PreAuthorize("hasRole('ADMIN')")
    public CareProductDto createCareProduct(@Valid @RequestBody CareProductSaveRequest request) {
        return catalogService.saveCareProduct(request);
    }

    @PutMapping("/care-products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CareProductDto updateCareProduct(@PathVariable String id, @Valid @RequestBody CareProductSaveRequest request) {
        return catalogService.saveCareProduct(new CareProductSaveRequest(id, request.title(), request.brand(), request.price(), request.icon()));
    }

    @DeleteMapping("/care-products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCareProduct(@PathVariable String id) {
        catalogService.deleteCareProduct(id);
    }

    @GetMapping("/drinks")
    public List<DrinkItemDto> listDrinks() {
        return catalogService.listDrinks();
    }

    @PostMapping("/drinks")
    @PreAuthorize("hasRole('ADMIN')")
    public DrinkItemDto createDrink(@Valid @RequestBody DrinkItemSaveRequest request) {
        return catalogService.saveDrink(request);
    }

    @PutMapping("/drinks/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DrinkItemDto updateDrink(@PathVariable String id, @Valid @RequestBody DrinkItemSaveRequest request) {
        return catalogService.saveDrink(new DrinkItemSaveRequest(id, request.title(), request.icon()));
    }

    @DeleteMapping("/drinks/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteDrink(@PathVariable String id) {
        catalogService.deleteDrink(id);
    }

    @GetMapping("/trends")
    public List<TrendDto> listTrends() {
        return catalogService.listTrends();
    }

    @PostMapping("/trends")
    @PreAuthorize("hasRole('ADMIN')")
    public TrendDto createTrend(@Valid @RequestBody TrendSaveRequest request) {
        return catalogService.saveTrend(request);
    }

    @PutMapping("/trends/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public TrendDto updateTrend(@PathVariable String id, @Valid @RequestBody TrendSaveRequest request) {
        return catalogService.saveTrend(new TrendSaveRequest(
                id,
                request.title(),
                request.description(),
                request.gradient(),
                request.emoji(),
                request.image()));
    }

    @DeleteMapping("/trends/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTrend(@PathVariable String id) {
        catalogService.deleteTrend(id);
    }
}
