package com.redhat.refarch.ecom.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Transaction {

    Long creditCardNumber
    Integer expMonth
    Integer expYear
    Integer verificationCode
    String billingAddress
    Long customerId
    String customerName
    Long orderNumber
    Double amount
}