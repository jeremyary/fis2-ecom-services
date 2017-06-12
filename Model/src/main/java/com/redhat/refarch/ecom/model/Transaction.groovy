package com.redhat.refarch.ecom.model

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

    Transaction() {}

    Transaction(Long creditCardNumber, Integer expMonth, Integer expYear, Integer verificationCode,
                String billingAddress, Long customerId, String customerName, Long orderNumber, Double amount) {
        this.creditCardNumber = creditCardNumber
        this.expMonth = expMonth
        this.expYear = expYear
        this.verificationCode = verificationCode
        this.billingAddress = billingAddress
        this.customerId = customerId
        this.customerName = customerName
        this.orderNumber = orderNumber
        this.amount = amount
    }
}