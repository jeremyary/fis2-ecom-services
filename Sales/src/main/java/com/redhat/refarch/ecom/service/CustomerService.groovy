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

    @Consume(uri = "amq:customers.get")
    Customer getCustomer(String customerId) {
        return customerRepository.getById(customerId)
    }

    @Consume(uri = "amq:customers.save")
    Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer)
    }

    @Consume(uri = "amq:customers.delete")
    void deleteCustomer(Customer customer) {
        customerRepository.delete(customer)
    }
    
    @Consume(uri = "amq:customers.authenticate")
    Customer authenticate(Customer customer) {

        Customer result = getCustomer(customer.getUsername())
        if (result.getPassword() != customer.getPassword()) {
            throw new WebApplicationException(HttpURLConnection.HTTP_UNAUTHORIZED)
        }
        return result
    }

    @Consume(uri = "amq:customers.orders.get")
    Order getOrder(String orderId) {
        return orderRepository.getById(orderId)
    }

    @Consume(uri = "amq:customers.orders.list")
    List<Order> listOrders(String customerId) {
        return orderRepository.findByCustomerId(customerId)
    }

    @Consume(uri = "amq:customers.orders.save")
    Order saveOrder(Order order) {
        return orderRepository.save(order)
    }

    @Consume(uri = "amq:customers.orders.delete")
    void deleteOrder(Order order) {
        orderRepository.delete(order)
    }

    @Consume(uri = "amq:customers.orders.orderItems.get")
    OrderItem getOrderItem(String orderItemId) {
        return orderItemRepository.getById(orderItemId)
    }

    @Consume(uri = "amq:customers.orders.orderItems.getAll")
    List<OrderItem> listOrderItems(String orderId) {

        List<String> orderItemIds = getOrder(orderId).getOrderItemIds()
        return orderItemIds.isEmpty() ? [] : orderItemRepository.findByIdIn(orderItemIds)
    }
    
    @Consume(uri = "amq:customers.orders.orderItems.save")
    OrderItem saveOrderItem(String orderId, OrderItem orderItem) {

        OrderItem result = orderItemRepository.save(orderItem)
        Order order = orderRepository.getById(orderId)
        order.getOrderItemIds().add(result.getId())
        orderRepository.save(order)
        return result
    }

    @Consume(uri = "amq:customers.orders.orderItems.delete")
    void deleteOrderItem(OrderItem orderItem) {
        orderItemRepository.delete(orderItem)
    }
}