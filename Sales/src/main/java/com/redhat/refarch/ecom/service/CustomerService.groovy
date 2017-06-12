package com.redhat.refarch.ecom.service

import com.redhat.refarch.ecom.model.Customer
import com.redhat.refarch.ecom.model.Order
import com.redhat.refarch.ecom.model.Order.Status
import com.redhat.refarch.ecom.model.OrderItem
import com.redhat.refarch.ecom.repository.CustomerRepository
import com.redhat.refarch.ecom.repository.OrderItemRepository
import com.redhat.refarch.ecom.repository.OrderRepository
import org.apache.camel.Consume
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.WebApplicationException

@Component
class CustomerService {

    @Autowired
    CustomerRepository customerRepository

    @Autowired
    OrderRepository orderRepository

    @Autowired
    OrderItemRepository orderItemRepository

    Customer getCustomer(String customerId) {
        Customer cust = customerRepository.getById(customerId)
        println "RETURNING CUSTOMER: ${cust.toString()}"
    }

    Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer)
    }

    void deleteCustomer(Customer customer) {
        customerRepository.delete(customer)
    }
    
    Customer authenticate(Customer customer) {

        Customer result = getCustomer(customer.getUsername())
        if (result.getPassword() != customer.getPassword()) {
            throw new WebApplicationException(HttpURLConnection.HTTP_UNAUTHORIZED)
        }
        return result
    }

    Order getOrder(String orderId) {
        return orderRepository.getById(orderId)
    }

    List<Order> listOrders(String customerId) {
        return orderRepository.findByCustomerId(customerId)
    }

    Order saveOrder(Order order) {
        return orderRepository.save(order)
    }

    void deleteOrder(Order order) {
        orderRepository.delete(order)
    }

    OrderItem getOrderItem(String orderItemId) {
        return orderItemRepository.getById(orderItemId)
    }

    List<OrderItem> listOrderItems(String orderId) {

        List<String> orderItemIds = getOrder(orderId).getOrderItemIds()
        return orderItemIds.isEmpty() ? [] : orderItemRepository.findByIdIn(orderItemIds)
    }
    
    OrderItem saveOrderItem(String orderId, OrderItem orderItem) {

        OrderItem result = orderItemRepository.save(orderItem)
        Order order = orderRepository.getById(orderId)
        order.getOrderItemIds().add(result.getId())
        orderRepository.save(order)
        return result
    }

    void deleteOrderItem(OrderItem orderItem) {
        orderItemRepository.delete(orderItem)
    }
}