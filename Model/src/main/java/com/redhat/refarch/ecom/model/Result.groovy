package com.redhat.refarch.ecom.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Result {

    static final Random random = new Random()

    enum Status {
        SUCCESS, FAILURE
    }

    Status status
    String name
    Long customerId
    Long orderNumber
    Date transactionDate
    Integer transactionNumber
}