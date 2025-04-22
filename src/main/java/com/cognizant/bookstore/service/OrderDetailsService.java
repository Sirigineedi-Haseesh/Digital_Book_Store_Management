package com.cognizant.bookstore.service;

import com.cognizant.bookstore.dto.OrderDetailsDTO;

import java.time.LocalDate;
import java.util.List;

public interface OrderDetailsService {

    List<OrderDetailsDTO> getOrderDetailsByDate(LocalDate date);

    List<OrderDetailsDTO> getOrderDetailsByDateRange(LocalDate startDate, LocalDate endDate);

    OrderDetailsDTO createOrder(OrderDetailsDTO orderDetailsDTO);

    String changeOrderStatus(Long id, String status);

    boolean isValidStatus(String status);
}
