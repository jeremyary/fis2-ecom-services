package com.redhat.refarch.ecom.model

import org.springframework.data.annotation.Id

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