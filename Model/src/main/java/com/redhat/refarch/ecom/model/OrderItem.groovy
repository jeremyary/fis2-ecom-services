package com.redhat.refarch.ecom.model

import groovy.transform.EqualsAndHashCode
import org.springframework.data.annotation.Id

@EqualsAndHashCode
class OrderItem {

    @Id
    String id
    Long sku
    Integer quantity

    OrderItem() {}
}