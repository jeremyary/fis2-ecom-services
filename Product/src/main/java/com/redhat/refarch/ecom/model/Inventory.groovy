package com.redhat.refarch.ecom.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Inventory implements Serializable {
    String sku
    int quantity

    Inventory(String sku, int quantity) {
        this.sku = sku
        this.quantity = quantity
    }
}