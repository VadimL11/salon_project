package org.example.salon_project.frontend.service;

import lombok.RequiredArgsConstructor;
import org.example.salon_project.domain.Client;
import org.example.salon_project.domain.FrontendCareOrder;
import org.example.salon_project.domain.FrontendCareOrderItem;
import org.example.salon_project.domain.FrontendDrinkOrder;
import org.example.salon_project.domain.Product;
import org.example.salon_project.exception.ResourceNotFoundException;
import org.example.salon_project.frontend.dto.CartItemDto;
import org.example.salon_project.frontend.dto.CareProductCheckoutRequest;
import org.example.salon_project.frontend.dto.CareProductCheckoutResponse;
import org.example.salon_project.frontend.dto.DrinkOrderRequest;
import org.example.salon_project.frontend.dto.DrinkOrderResponse;
import org.example.salon_project.frontend.mapper.FrontendMapper;
import org.example.salon_project.repository.ClientRepository;
import org.example.salon_project.repository.DrinkRepository;
import org.example.salon_project.repository.FrontendCareOrderRepository;
import org.example.salon_project.repository.FrontendDrinkOrderRepository;
import org.example.salon_project.repository.ProductRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FrontendCheckoutService {

    private final ProductRepository productRepository;
    private final DrinkRepository drinkRepository;
    private final ClientRepository clientRepository;
    private final FrontendCareOrderRepository frontendCareOrderRepository;
    private final FrontendDrinkOrderRepository frontendDrinkOrderRepository;
    private final FrontendMapper mapper;

    @Transactional
    public CareProductCheckoutResponse checkout(CareProductCheckoutRequest request, String authenticatedExternalId) {
        Client client = resolveAuthenticatedClient(authenticatedExternalId);
        List<CartItemDto> canonicalItems = request.items().stream()
                .map(this::resolveCartItem)
                .toList();

        BigDecimal total = canonicalItems.stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        FrontendCareOrder order = FrontendCareOrder.builder()
                .externalId(makeId("care-order"))
                .client(client)
                .customerEmail(resolveCustomerEmail(client))
                .totalAmount(total)
                .build();

        List<FrontendCareOrderItem> items = new ArrayList<>();
        for (int index = 0; index < canonicalItems.size(); index++) {
            CartItemDto item = canonicalItems.get(index);
            items.add(FrontendCareOrderItem.builder()
                    .careOrder(order)
                    .productExternalId(item.id())
                    .itemName(item.name())
                    .unitPrice(item.price())
                    .quantity(item.quantity())
                    .sortOrder(index)
                    .build());
        }
        order.getItems().addAll(items);

        FrontendCareOrder saved = frontendCareOrderRepository.save(order);
        return new CareProductCheckoutResponse(
                true,
                saved.getExternalId(),
                saved.getCustomerEmail(),
                saved.getClient() != null ? saved.getClient().getFirstName() : null,
                saved.getClient() != null ? saved.getClient().getLastName() : null,
                saved.getClient() != null ? saved.getClient().getPhone() : null,
                saved.getItems().stream().map(mapper::toDto).toList(),
                saved.getTotalAmount(),
                saved.getCreatedAt().toString());
    }

    @Transactional
    public DrinkOrderResponse orderDrink(DrinkOrderRequest request, String authenticatedExternalId) {
        drinkRepository.findByExternalId(request.drinkId())
                .orElseThrow(() -> new IllegalArgumentException("Unknown drink: " + request.drinkId()));

        Client client = resolveAuthenticatedClient(authenticatedExternalId);
        FrontendDrinkOrder order = frontendDrinkOrderRepository.save(FrontendDrinkOrder.builder()
                .externalId(makeId("drink-order"))
                .client(client)
                .customerEmail(resolveCustomerEmail(client))
                .drinkExternalId(request.drinkId())
                .build());

        return new DrinkOrderResponse(
                true,
                order.getExternalId(),
                order.getCustomerEmail(),
                order.getDrinkExternalId(),
                order.getCreatedAt().toString());
    }

    @Transactional
    public void cancelCareOrder(String externalId, String authenticatedExternalId, boolean admin) {
        FrontendCareOrder order = frontendCareOrderRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("CareOrder", externalId));
        validateOrderAccess(order.getClient(), order.getCustomerEmail(), authenticatedExternalId, admin);
        frontendCareOrderRepository.delete(order);
    }

    @Transactional
    public void cancelDrinkOrder(String externalId, String authenticatedExternalId, boolean admin) {
        FrontendDrinkOrder order = frontendDrinkOrderRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("DrinkOrder", externalId));
        validateOrderAccess(order.getClient(), order.getCustomerEmail(), authenticatedExternalId, admin);
        frontendDrinkOrderRepository.delete(order);
    }

    private CartItemDto resolveCartItem(CartItemDto item) {
        Product product = productRepository.findByExternalId(item.id())
                .orElseThrow(() -> new IllegalArgumentException("Unknown care product: " + item.id()));
        return new CartItemDto(
                product.getExternalId(),
                item.name(),
                product.getPrice(),
                item.quantity());
    }

    private Client resolveAuthenticatedClient(String externalId) {
        if (externalId == null || externalId.isBlank()) {
            return null;
        }
        return clientRepository.findByExternalId(externalId).orElse(null);
    }

    private void validateOrderAccess(Client linkedClient, String customerEmail, String authenticatedExternalId, boolean admin) {
        if (admin) {
            return;
        }

        Client client = resolveAuthenticatedClient(authenticatedExternalId);
        if (client == null) {
            throw new AccessDeniedException("Authentication required");
        }
        if (!belongsToClient(linkedClient, customerEmail, client)) {
            throw new AccessDeniedException("You can only cancel your own orders");
        }
    }

    private boolean belongsToClient(Client linkedClient, String customerEmail, Client authenticatedClient) {
        if (linkedClient != null && authenticatedClient.getExternalId().equals(linkedClient.getExternalId())) {
            return true;
        }
        return equalsNormalizedEmail(customerEmail, authenticatedClient.getEmail());
    }

    private boolean equalsNormalizedEmail(String left, String right) {
        String normalizedLeft = normalizeEmail(left);
        String normalizedRight = normalizeEmail(right);
        return !normalizedLeft.isBlank() && normalizedLeft.equals(normalizedRight);
    }

    private String normalizeEmail(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private String resolveCustomerEmail(Client client) {
        return client == null ? null : client.getEmail();
    }

    private String makeId(String prefix) {
        return prefix + "-" + UUID.randomUUID();
    }
}
