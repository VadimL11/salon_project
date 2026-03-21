package org.example.salon_project.frontend.service;

import lombok.RequiredArgsConstructor;
import org.example.salon_project.domain.BeautyService;
import org.example.salon_project.domain.BookingSlot;
import org.example.salon_project.domain.Category;
import org.example.salon_project.domain.Client;
import org.example.salon_project.domain.Drink;
import org.example.salon_project.domain.Master;
import org.example.salon_project.domain.MasterCredential;
import org.example.salon_project.domain.Product;
import org.example.salon_project.domain.Trend;
import org.example.salon_project.exception.ResourceNotFoundException;
import org.example.salon_project.frontend.dto.BookingSlotDto;
import org.example.salon_project.frontend.dto.BookingSlotSaveRequest;
import org.example.salon_project.frontend.dto.CareProductDto;
import org.example.salon_project.frontend.dto.CareProductSaveRequest;
import org.example.salon_project.frontend.dto.DrinkItemDto;
import org.example.salon_project.frontend.dto.DrinkItemSaveRequest;
import org.example.salon_project.frontend.dto.FrontendBootstrapDto;
import org.example.salon_project.frontend.dto.MasterDto;
import org.example.salon_project.frontend.dto.MasterCredentialSaveRequest;
import org.example.salon_project.frontend.dto.MasterSaveRequest;
import org.example.salon_project.frontend.dto.ServiceCategoryDto;
import org.example.salon_project.frontend.dto.ServiceCategorySaveRequest;
import org.example.salon_project.frontend.dto.ServiceItemDto;
import org.example.salon_project.frontend.dto.ServiceItemSaveRequest;
import org.example.salon_project.frontend.dto.TrendDto;
import org.example.salon_project.frontend.dto.TrendSaveRequest;
import org.example.salon_project.frontend.mapper.FrontendMapper;
import org.example.salon_project.repository.BeautyServiceRepository;
import org.example.salon_project.repository.BookingSlotRepository;
import org.example.salon_project.repository.CategoryRepository;
import org.example.salon_project.repository.ClientRepository;
import org.example.salon_project.repository.DrinkRepository;
import org.example.salon_project.repository.FrontendCareOrderRepository;
import org.example.salon_project.repository.FrontendDrinkOrderRepository;
import org.example.salon_project.repository.MasterRepository;
import org.example.salon_project.repository.ProductRepository;
import org.example.salon_project.repository.SalonBookingRepository;
import org.example.salon_project.repository.TrendRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FrontendCatalogService {

    private static final List<String> VALID_PERIODS = List.of("morning", "afternoon", "evening");

    private final CategoryRepository categoryRepository;
    private final BeautyServiceRepository beautyServiceRepository;
    private final MasterRepository masterRepository;
    private final BookingSlotRepository bookingSlotRepository;
    private final ProductRepository productRepository;
    private final DrinkRepository drinkRepository;
    private final TrendRepository trendRepository;
    private final SalonBookingRepository salonBookingRepository;
    private final ClientRepository clientRepository;
    private final FrontendCareOrderRepository frontendCareOrderRepository;
    private final FrontendDrinkOrderRepository frontendDrinkOrderRepository;
    private final FrontendMapper mapper;

    public FrontendBootstrapDto bootstrap(String authenticatedExternalId, boolean admin, boolean guest) {
        Client authenticatedClient = admin || guest ? null : findClient(authenticatedExternalId);
        String guestExternalId = guest ? authenticatedExternalId : null;
        return new FrontendBootstrapDto(
                listCategories(),
                listServices(),
                listMasters(),
                listBookingSlots(),
                listCareProducts(),
                listDrinks(),
                listTrends(),
                listBookings(authenticatedClient, guestExternalId, admin),
                listCareOrders(authenticatedClient, guestExternalId, admin),
                listDrinkOrders(authenticatedClient, guestExternalId, admin));
    }

    public List<ServiceCategoryDto> listCategories() {
        return categoryRepository.findByExternalIdIsNotNullOrderBySortOrderAsc().stream().map(mapper::toDto).toList();
    }

    public List<ServiceItemDto> listServices() {
        return beautyServiceRepository.findByExternalIdIsNotNullOrderBySortOrderAsc().stream().map(mapper::toDto).toList();
    }

    public List<MasterDto> listMasters() {
        return masterRepository.findByExternalIdIsNotNullOrderBySortOrderAsc().stream().map(mapper::toDto).toList();
    }

    public List<BookingSlotDto> listBookingSlots() {
        return bookingSlotRepository.findAllByOrderBySortOrderAsc().stream().map(mapper::toDto).toList();
    }

    public List<CareProductDto> listCareProducts() {
        return productRepository.findByExternalIdIsNotNullOrderBySortOrderAsc().stream().map(mapper::toDto).toList();
    }

    public List<DrinkItemDto> listDrinks() {
        return drinkRepository.findByExternalIdIsNotNullOrderBySortOrderAsc().stream().map(mapper::toDto).toList();
    }

    public List<TrendDto> listTrends() {
        return trendRepository.findAllByOrderBySortOrderAsc().stream().map(mapper::toDto).toList();
    }

    @Transactional
    public ServiceCategoryDto saveCategory(ServiceCategorySaveRequest request) {
        Category category = request.id() == null
                ? new Category()
                : findCategory(request.id());

        if (category.getId() == null) {
            category.setExternalId(defaultId(request.id(), "cat"));
            category.setSortOrder(nextSortOrder(categoryRepository.findByExternalIdIsNotNullOrderBySortOrderAsc().stream()
                    .map(Category::getSortOrder)
                    .toList()));
        }

        category.setSlug(request.slug().trim());
        category.setIcon(request.icon().trim());
        category.setTitle(mapper.toEmbeddable(request.title()));
        category.setLocalizedDescription(mapper.toEmbeddable(request.description()));
        category.setName(primaryText(request.title()));
        category.setDescription(primaryText(request.description()));
        return mapper.toDto(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(String externalId) {
        categoryRepository.delete(findCategory(externalId));
    }

    @Transactional
    public ServiceItemDto saveService(ServiceItemSaveRequest request) {
        Category category = findCategory(request.categoryId());
        BeautyService service = request.id() == null
                ? new BeautyService()
                : findService(request.id());

        if (service.getId() == null) {
            service.setExternalId(defaultId(request.id(), "service"));
            service.setSortOrder(nextSortOrder(beautyServiceRepository.findByExternalIdIsNotNullOrderBySortOrderAsc().stream()
                    .map(BeautyService::getSortOrder)
                    .toList()));
            service.setActive(true);
        }

        service.setCategory(category);
        service.setTitle(mapper.toEmbeddable(request.title()));
        service.setName(primaryText(request.title()));
        service.setDurationMinutes(request.durationMinutes());
        service.setPrice(defaultPrice(request.price()));
        return mapper.toDto(beautyServiceRepository.save(service));
    }

    @Transactional
    public void deleteService(String externalId) {
        beautyServiceRepository.delete(findService(externalId));
    }

    @Transactional
    public MasterDto saveMaster(MasterSaveRequest request) {
        Master master = request.id() == null
                ? new Master()
                : findMaster(request.id());

        if (master.getId() == null) {
            master.setExternalId(defaultId(request.id(), "master"));
            master.setSortOrder(nextSortOrder(masterRepository.findByExternalIdIsNotNullOrderBySortOrderAsc().stream()
                    .map(Master::getSortOrder)
                    .toList()));
            master.setActive(true);
        }

        String[] nameParts = splitName(request.name());
        master.setFirstName(nameParts[0]);
        master.setLastName(nameParts[1]);
        master.setDisplayName(request.name().trim());
        master.setRole(mapper.toEmbeddable(request.role()));
        master.setInitials(request.initials().trim());
        master.setExperienceLabel(request.experienceLabel().trim());
        master.setSpecialtyCategories(new LinkedHashSet<>(resolveCategories(request.specialtyCategoryIds())));
        syncCredentials(master, request.credentials());
        return mapper.toDto(masterRepository.save(master));
    }

    @Transactional
    public void deleteMaster(String externalId) {
        masterRepository.delete(findMaster(externalId));
    }

    @Transactional
    public BookingSlotDto saveBookingSlot(BookingSlotSaveRequest request) {
        BookingSlot slot = request.id() == null
                ? new BookingSlot()
                : findBookingSlot(request.id());

        if (slot.getId() == null) {
            slot.setExternalId(defaultId(request.id(), "slot"));
            slot.setSortOrder(nextSortOrder(bookingSlotRepository.findAllByOrderBySortOrderAsc().stream()
                    .map(BookingSlot::getSortOrder)
                    .toList()));
        }

        String period = request.period().trim().toLowerCase();
        if (!VALID_PERIODS.contains(period)) {
            throw new IllegalArgumentException("period must be one of: morning, afternoon, evening");
        }
        slot.setPeriod(period);
        try {
            slot.setTime(mapper.parseTime(request.time()));
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("time must use HH:mm format");
        }
        return mapper.toDto(bookingSlotRepository.save(slot));
    }

    @Transactional
    public void deleteBookingSlot(String externalId) {
        bookingSlotRepository.delete(findBookingSlot(externalId));
    }

    @Transactional
    public CareProductDto saveCareProduct(CareProductSaveRequest request) {
        Product product = request.id() == null
                ? new Product()
                : findProduct(request.id());

        if (product.getId() == null) {
            product.setExternalId(defaultId(request.id(), "product"));
            product.setSortOrder(nextSortOrder(productRepository.findByExternalIdIsNotNullOrderBySortOrderAsc().stream()
                    .map(Product::getSortOrder)
                    .toList()));
            product.setStock(100);
        }

        product.setTitle(mapper.toEmbeddable(request.title()));
        product.setName(primaryText(request.title()));
        product.setBrand(request.brand().trim());
        product.setPrice(defaultPrice(request.price()));
        product.setIcon(request.icon().trim());
        return mapper.toDto(productRepository.save(product));
    }

    @Transactional
    public void deleteCareProduct(String externalId) {
        productRepository.delete(findProduct(externalId));
    }

    @Transactional
    public DrinkItemDto saveDrink(DrinkItemSaveRequest request) {
        Drink drink = request.id() == null
                ? new Drink()
                : findDrink(request.id());

        if (drink.getId() == null) {
            drink.setExternalId(defaultId(request.id(), "drink"));
            drink.setSortOrder(nextSortOrder(drinkRepository.findByExternalIdIsNotNullOrderBySortOrderAsc().stream()
                    .map(Drink::getSortOrder)
                    .toList()));
            drink.setAvailable(true);
            drink.setPrice(BigDecimal.ZERO);
        }

        drink.setTitle(mapper.toEmbeddable(request.title()));
        drink.setName(primaryText(request.title()));
        drink.setIcon(request.icon().trim());
        return mapper.toDto(drinkRepository.save(drink));
    }

    @Transactional
    public void deleteDrink(String externalId) {
        drinkRepository.delete(findDrink(externalId));
    }

    @Transactional
    public TrendDto saveTrend(TrendSaveRequest request) {
        Trend trend = request.id() == null
                ? new Trend()
                : findTrend(request.id());

        if (trend.getId() == null) {
            trend.setExternalId(defaultId(request.id(), "trend"));
            trend.setSortOrder(nextSortOrder(trendRepository.findAllByOrderBySortOrderAsc().stream()
                    .map(Trend::getSortOrder)
                    .toList()));
        }

        trend.setTitle(mapper.toEmbeddable(request.title()));
        trend.setDescription(mapper.toEmbeddable(request.description()));
        trend.setGradient(request.gradient().trim());
        trend.setEmoji(request.emoji().trim());
        trend.setImageUrl(blankToNull(request.image()));
        return mapper.toDto(trendRepository.save(trend));
    }

    private List<org.example.salon_project.frontend.dto.BookingRecordDto> listBookings(Client authenticatedClient, String guestExternalId, boolean admin) {
        return salonBookingRepository.findAllByOrderBySortOrderAsc().stream()
                .filter(booking -> admin
                        || (authenticatedClient != null && belongsToClient(booking, authenticatedClient))
                        || belongsToGuest(booking.getGuestExternalId(), guestExternalId))
                .map(mapper::toDto)
                .toList();
    }

    private List<org.example.salon_project.frontend.dto.CareOrderRecordDto> listCareOrders(Client authenticatedClient, String guestExternalId, boolean admin) {
        return frontendCareOrderRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(order -> admin
                        || (authenticatedClient != null && belongsToClient(order.getClient(), order.getCustomerEmail(), authenticatedClient))
                        || belongsToGuest(order.getGuestExternalId(), guestExternalId))
                .map(mapper::toDto)
                .toList();
    }

    private List<org.example.salon_project.frontend.dto.DrinkOrderRecordDto> listDrinkOrders(Client authenticatedClient, String guestExternalId, boolean admin) {
        return frontendDrinkOrderRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(order -> admin
                        || (authenticatedClient != null && belongsToClient(order.getClient(), order.getCustomerEmail(), authenticatedClient))
                        || belongsToGuest(order.getGuestExternalId(), guestExternalId))
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteTrend(String externalId) {
        trendRepository.delete(findTrend(externalId));
    }

    private Category findCategory(String externalId) {
        return categoryRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", externalId));
    }

    private BeautyService findService(String externalId) {
        return beautyServiceRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Service", externalId));
    }

    private Master findMaster(String externalId) {
        return masterRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Master", externalId));
    }

    private BookingSlot findBookingSlot(String externalId) {
        return bookingSlotRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("BookingSlot", externalId));
    }

    private Product findProduct(String externalId) {
        return productRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", externalId));
    }

    private Drink findDrink(String externalId) {
        return drinkRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Drink", externalId));
    }

    private Trend findTrend(String externalId) {
        return trendRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Trend", externalId));
    }

    private List<Category> resolveCategories(List<String> externalIds) {
        if (externalIds == null || externalIds.isEmpty()) {
            return List.of();
        }
        return externalIds.stream().map(this::findCategory).toList();
    }

    private void syncCredentials(Master master, List<MasterCredentialSaveRequest> requests) {
        List<MasterCredentialSaveRequest> nextRequests = requests == null ? List.of() : requests;
        Map<String, MasterCredential> existingById = master.getCredentials().stream()
                .filter(entry -> entry.getExternalId() != null && !entry.getExternalId().isBlank())
                .collect(Collectors.toMap(MasterCredential::getExternalId, Function.identity(), (left, ignored) -> left));

        List<MasterCredential> nextCredentials = new ArrayList<>();
        for (int index = 0; index < nextRequests.size(); index++) {
            MasterCredentialSaveRequest request = nextRequests.get(index);
            String externalId = defaultId(request.id(), "credential");
            MasterCredential credential = existingById.getOrDefault(externalId, new MasterCredential());
            credential.setExternalId(externalId);
            credential.setMaster(master);
            credential.setName(request.name().trim());
            credential.setType(request.type().trim());
            credential.setFileUrl(request.fileUrl().trim());
            credential.setSortOrder(index);
            nextCredentials.add(credential);
        }

        master.getCredentials().clear();
        master.getCredentials().addAll(nextCredentials);
    }

    private Client findClient(String externalId) {
        if (externalId == null || externalId.isBlank()) {
            return null;
        }
        return clientRepository.findByExternalId(externalId).orElse(null);
    }

    private boolean belongsToClient(org.example.salon_project.domain.SalonBooking booking, Client client) {
        if (booking.getClient() != null && client.getExternalId().equals(booking.getClient().getExternalId())) {
            return true;
        }
        if (equalsNormalizedEmail(booking.getCustomerEmail(), client.getEmail())) {
            return true;
        }
        return equalsNormalizedPhone(booking.getCustomerPhone(), client.getPhone());
    }

    private boolean belongsToClient(Client linkedClient, String customerEmail, Client authenticatedClient) {
        if (linkedClient != null && authenticatedClient.getExternalId().equals(linkedClient.getExternalId())) {
            return true;
        }
        return equalsNormalizedEmail(customerEmail, authenticatedClient.getEmail());
    }

    private boolean belongsToGuest(String linkedGuestExternalId, String authenticatedGuestExternalId) {
        return authenticatedGuestExternalId != null
                && !authenticatedGuestExternalId.isBlank()
                && authenticatedGuestExternalId.equals(linkedGuestExternalId);
    }

    private String primaryText(org.example.salon_project.frontend.dto.LocalizedTextDto value) {
        if (value.GB() != null && !value.GB().isBlank()) {
            return value.GB().trim();
        }
        if (value.DE() != null && !value.DE().isBlank()) {
            return value.DE().trim();
        }
        return value.UA() == null ? "" : value.UA().trim();
    }

    private BigDecimal defaultPrice(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String[] splitName(String value) {
        String trimmed = value.trim();
        int idx = trimmed.indexOf(' ');
        if (idx < 0) {
            return new String[] { trimmed, "" };
        }
        return new String[] { trimmed.substring(0, idx), trimmed.substring(idx + 1).trim() };
    }

    private String defaultId(String requestedId, String prefix) {
        return requestedId != null && !requestedId.isBlank() ? requestedId : prefix + "-" + UUID.randomUUID();
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

    private Integer nextSortOrder(List<Integer> sortOrders) {
        if (sortOrders.isEmpty()) {
            return 0;
        }
        return sortOrders.getFirst() - 1;
    }
}
