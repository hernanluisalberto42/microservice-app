package com.microservice.orderservice.service;

import com.microservice.orderservice.dto.OrderDto;

public interface OrderService {

    public String placeOrder(OrderDto orderDto);
}
