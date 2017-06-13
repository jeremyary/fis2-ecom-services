package com.redhat.refarch.ecom.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Result {

    enum Status {
        SUCCESS, FAILURE
    }

    Status status
    String name
    String customerId
    String orderNumber
    Long transactionDate
    Integer transactionNumber
}