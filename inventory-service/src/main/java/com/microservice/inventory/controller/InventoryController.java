package com.microservice.inventory.controller;

import com.microservice.inventory.dto.InventoryDto;
import com.microservice.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryDto> isInStock(@RequestParam List<String> skuCode) throws InterruptedException {
         return inventoryService.inInStock(skuCode);
    }
}
