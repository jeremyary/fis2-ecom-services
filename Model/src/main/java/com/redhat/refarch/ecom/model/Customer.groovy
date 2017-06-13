package com.redhat.refarch.ecom.model

import groovy.transform.EqualsAndHashCode
import org.springframework.data.annotation.Id

@EqualsAndHashCode
class Customer {

    @Id
    String id
    String name
    String address
    String telephone
    String email
    String username
    String password

    Customer() {}

    Customer(String name, String address, String telephone, String email, String username, String password) {
        this.name = name
        this.address = address
        this.telephone = telephone
        this.email = email
        this.username = username
        this.password = password
    }
}