package com.redhat.refarch.ecom.model

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

    Result(String name, Long customerId, Long orderNumber) {
        this.name = name
        this.customerId = customerId
        this.orderNumber = orderNumber
        this.transactionNumber = random.nextInt(9000000) + 1000000
        this.transactionDate = Calendar.getInstance().getTime()
    }
}