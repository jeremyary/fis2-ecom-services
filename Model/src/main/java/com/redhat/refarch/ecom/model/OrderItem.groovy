package com.redhat.refarch.ecom.model

import groovy.transform.EqualsAndHashCode
import org.springframework.data.annotation.Id

@EqualsAndHashCode
class OrderItem {

    @Id
    String id
    String sku
    Integer quantity

    OrderItem() {}
}