package com.microservice.inventory.service;

import com.microservice.inventory.dto.InventoryDto;

import java.util.List;

public interface InventoryService {
    public List<InventoryDto> inInStock(List<String> skuCodeList) throws InterruptedException;
}
