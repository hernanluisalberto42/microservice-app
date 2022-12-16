package com.microservice.inventory.service;

import com.microservice.inventory.dto.InventoryDto;
import com.microservice.inventory.repository.InventoryRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Transactional
public class InventoryServiceImp implements InventoryService{

    @Autowired
    private InventoryRepository inventoryRepository;


    @Transactional(readOnly = true)
    public List<InventoryDto> inInStock(List<String> skuCodeList) throws InterruptedException {
        log.info("Wait started");
        Thread.sleep(10000);
        log.info("Wait end");
        return inventoryRepository.findBySkuCodeIn(skuCodeList)
                .stream()
                .map(inventory ->
                    InventoryDto.builder()
                            .skuCode(inventory.getSkuCode())
                            .isInStock(inventory.getQuantity() > 0)
                            .build()
                ).toList();
    }
}
