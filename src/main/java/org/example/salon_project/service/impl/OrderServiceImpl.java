package org.example.salon_project.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.salon_project.domain.*;
import org.example.salon_project.dto.*;
import org.example.salon_project.exception.ResourceNotFoundException;
import org.example.salon_project.mapper.DrinkOrderMapper;
import org.example.salon_project.mapper.ProductOrderMapper;
import org.example.salon_project.repository.*;
import org.example.salon_project.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final AppointmentRepository appointmentRepo;
    private final ProductRepository productRepo;
    private final DrinkRepository drinkRepo;
    private final ProductOrderRepository productOrderRepo;
    private final DrinkOrderRepository drinkOrderRepo;
    private final ProductOrderMapper productOrderMapper;
    private final DrinkOrderMapper drinkOrderMapper;

    @Override
    @Transactional
    public ProductOrderDto addProductOrder(Long appointmentId, ProductOrderCreateRequest request) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));
        Product product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", request.getProductId()));

        ProductOrder order = new ProductOrder();
        order.setAppointment(appointment);
        order.setProduct(product);
        order.setQuantity(request.getQuantity());
        order.setPrice(request.getPrice());
        return productOrderMapper.toDto(productOrderRepo.save(order));
    }

    @Override
    @Transactional
    public DrinkOrderDto addDrinkOrder(Long appointmentId, DrinkOrderCreateRequest request) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));
        Drink drink = drinkRepo.findById(request.getDrinkId())
                .orElseThrow(() -> new ResourceNotFoundException("Drink", request.getDrinkId()));

        DrinkOrder order = new DrinkOrder();
        order.setAppointment(appointment);
        order.setDrink(drink);
        order.setQuantity(request.getQuantity());
        order.setPrice(request.getPrice());
        return drinkOrderMapper.toDto(drinkOrderRepo.save(order));
    }
}
