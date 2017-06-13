package com.redhat.refarch.ecom.model

import groovy.transform.EqualsAndHashCode
import org.springframework.data.annotation.Id

@EqualsAndHashCode
class Order {
    enum Status
    {
        Initial, InProgress, Canceled, Paid, Shipped, Completed
    }

    @Id
    String id
    Status status
    Long transactionNumber
    Date transactionDate
    String customerId
    List<String> orderItemIds = []

    Order() {}
}