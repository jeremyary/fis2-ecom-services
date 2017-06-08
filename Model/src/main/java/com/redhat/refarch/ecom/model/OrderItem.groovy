package com.redhat.refarch.ecom.model

import org.springframework.data.annotation.Id

class OrderItem {

    @Id
    String id
    Long sku
    Integer quantity

    OrderItem(Long id, Long sku, Integer quantity) {
        this.id = id
        this.sku = sku
        this.quantity = quantity
    }
}