package com.redhat.refarch.ecom.service

import com.redhat.refarch.ecom.model.Customer
import com.redhat.refarch.ecom.model.Order
import com.redhat.refarch.ecom.model.Order.Status
import com.redhat.refarch.ecom.model.OrderItem
import com.redhat.refarch.ecom.repository.CustomerRepository
import com.redhat.refarch.ecom.repository.OrderItemRepository
import com.redhat.refarch.ecom.repository.OrderRepository
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

    Customer addCustomer(Customer customer) {
        return customerRepository.save(customer)
    }

    Customer getCustomerByUsername(String username) {
        return customerRepository.getByUsername(username)
    }

    Customer getCustomer(String id) {
        return customerRepository.getById(id)
    }

    Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer)
    }

    void deleteCustomer(String id) {
        customerRepository.delete(customerRepository.getById(id))
    }

    static Order addOrder(String customerId, Order order) {
        return order.setCustomerId(customerId)
    }

    List<Order> getOrders(String customerId, Status status) {
        return (status == null) ? orderRepository.findByCustomerId(customerId) : orderRepository.findByStatus(status)
    }

    Order getOrder(String customerId, String orderId) {
        return orderRepository.getById(orderId)
    }

    Order saveOrder(Order order) {
        return orderRepository.save(order)
    }

    void deleteOrder(String orderId) {
        orderRepository.delete(orderRepository.getById(orderId))
    }

    OrderItem saveOrderItem(String orderId, OrderItem orderItem) {

        OrderItem result = orderItemRepository.save(orderItem)
        Order order = orderRepository.getById(orderId)
        order.getOrderItemIds().add(result.getId())
        orderRepository.save(order)
        return result
    }

    List<OrderItem> getOrderItems(String customerId, String orderId) {

        List<String> orderItemIds = getOrder(customerId, orderId).getOrderItemIds()
        return orderItemIds.isEmpty() ? [] : orderItemRepository.findByIdIn(orderItemIds)
    }

    OrderItem getOrderItem(String orderItemId) {
        return orderItemRepository.getById(orderItemId)
    }

    OrderItem saveOrderItemById(OrderItem orderItem) {
        return orderItemRepository.save(orderItem)
    }

    void deleteOrderItem(String orderItemId) {
        orderItemRepository.delete(orderItemRepository.getById(orderItemId))
    }

    Customer authenticate(Customer customer) {

        Customer result = getCustomer(customer.getUsername())
        if (result.getPassword() != customer.getPassword()) {
            throw new WebApplicationException(HttpURLConnection.HTTP_UNAUTHORIZED)
        }
        return result
    }
}