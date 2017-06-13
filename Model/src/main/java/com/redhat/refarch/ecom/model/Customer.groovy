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
}