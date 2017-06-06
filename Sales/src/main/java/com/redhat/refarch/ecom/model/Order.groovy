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

    Order(Long id, Status status, Long transactionNumber, Date transactionDate, String customerId, List<String>
            orderItemIds) {
        this.id = id
        this.status = status
        this.transactionNumber = transactionNumber
        this.transactionDate = transactionDate
        this.customerId = customerId
        this.orderItemIds = orderItemIds
    }
}