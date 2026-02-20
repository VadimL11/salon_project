package org.example.salon_project.service;

import org.example.salon_project.dto.*;

public interface OrderService {
    ProductOrderDto addProductOrder(Long appointmentId, ProductOrderCreateRequest request);

    DrinkOrderDto addDrinkOrder(Long appointmentId, DrinkOrderCreateRequest request);
}
