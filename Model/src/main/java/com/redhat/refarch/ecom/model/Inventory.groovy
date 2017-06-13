package com.redhat.refarch.ecom.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Inventory {
    String sku
    int quantity

    Inventory() {}

    Inventory(String sku, int quantity) {
        this.sku = sku
        this.quantity = quantity
    }
}