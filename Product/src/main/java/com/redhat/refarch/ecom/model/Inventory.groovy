package com.redhat.refarch.ecom.model

class Inventory {
    String sku
    int quantity

    Inventory(String sku, int quantity) {
        this.sku = sku
        this.quantity = quantity
    }
}