package com.microservice.orderservice.service;

import com.microservice.orderservice.dto.InventoryDto;
import com.microservice.orderservice.dto.OrderDto;
import com.microservice.orderservice.dto.OrderLineItemsDto;
import com.microservice.orderservice.event.OrderPlacedEvent;
import com.microservice.orderservice.model.Order;
import com.microservice.orderservice.model.OrderLineItems;
import com.microservice.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImp implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private Tracer tracer;

    @Autowired
    private KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;

    public String placeOrder(OrderDto orderDto){
        Order order=new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLinesItems = orderDto.getOrderLineItemsDtoList()
                .stream()
                .map(this::convertTEntity)
                .collect(Collectors.toList());
        order.setOrderLineItems(orderLinesItems);
        List<String> skuCodes = order.getOrderLineItems()
                .stream()
                .map(OrderLineItems::getSkuCode)
                .toList();
        Span inventoryServiceLookup = tracer.nextSpan().name("InventoryServiceLookup");

        try(Tracer.SpanInScope spanInScope=tracer.withSpan(inventoryServiceLookup.start())) {
            InventoryDto[] inventoryDtos = webClientBuilder.build().get()
                    .uri("http://INVENTORY-SERVICE/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryDto[].class)
                    .block();
            boolean allProductsInStock = Arrays.stream(inventoryDtos).allMatch(InventoryDto::getIsInStock);
            if (!allProductsInStock) {
                throw new IllegalArgumentException("Product is not in Stock, please try again");
            } else {
                orderRepository.save(order);
                kafkaTemplate.send("notificationTopic",new OrderPlacedEvent(order.getOrderNumber()));
                return "Order Placed Successfully";
            }
        }finally {
            inventoryServiceLookup.end();
        }

    }

    private OrderLineItems convertTEntity(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems=new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
