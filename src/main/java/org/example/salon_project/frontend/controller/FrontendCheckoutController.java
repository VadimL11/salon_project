package org.example.salon_project.frontend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.salon_project.frontend.dto.CareProductCheckoutRequest;
import org.example.salon_project.frontend.dto.CareProductCheckoutResponse;
import org.example.salon_project.frontend.dto.DrinkOrderRequest;
import org.example.salon_project.frontend.dto.DrinkOrderResponse;
import org.example.salon_project.frontend.service.FrontendCheckoutService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/frontend")
@RequiredArgsConstructor
public class FrontendCheckoutController {

    private final FrontendCheckoutService checkoutService;

    @PostMapping("/care-product-checkouts")
    public CareProductCheckoutResponse checkout(@Valid @RequestBody CareProductCheckoutRequest request, Authentication authentication) {
        return checkoutService.checkout(request, authentication != null ? authentication.getName() : null);
    }

    @PostMapping("/drink-orders")
    public DrinkOrderResponse orderDrink(@Valid @RequestBody DrinkOrderRequest request, Authentication authentication) {
        return checkoutService.orderDrink(request, authentication != null ? authentication.getName() : null);
    }

    @DeleteMapping("/care-product-checkouts/{id}")
    public void cancelCareOrder(@PathVariable String id, Authentication authentication) {
        boolean admin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        checkoutService.cancelCareOrder(id, authentication != null ? authentication.getName() : null, admin);
    }

    @DeleteMapping("/drink-orders/{id}")
    public void cancelDrinkOrder(@PathVariable String id, Authentication authentication) {
        boolean admin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        checkoutService.cancelDrinkOrder(id, authentication != null ? authentication.getName() : null, admin);
    }
}
