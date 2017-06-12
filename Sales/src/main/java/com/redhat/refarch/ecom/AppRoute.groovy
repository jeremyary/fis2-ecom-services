/**
 * Copyright 2005-2017 Red Hat, Inc.
 * <p>
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License") you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.redhat.refarch.ecom

import com.redhat.refarch.ecom.model.Customer
import com.redhat.refarch.ecom.model.Order
import com.redhat.refarch.ecom.model.OrderItem
import com.redhat.refarch.ecom.service.CustomerService
import org.apache.camel.model.dataformat.JsonLibrary
import org.apache.camel.spring.SpringRouteBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AppRoute extends SpringRouteBuilder {

    @Autowired
    CustomerService customerService

    @Override
    void configure() throws Exception {

        from("amq:customers.get")
                .bean(customerService, 'getCustomer(${header.customerId})')
                .to("log:customers.get?level=DEBUG")
                .marshal().json(JsonLibrary.Jackson)
                .to("log:customers.get?level=DEBUG")

        from("amq:customers.save")
                .unmarshal().json(JsonLibrary.Jackson, Customer.class)
                .bean(customerService, "saveCustomer")
                .marshal().json(JsonLibrary.Jackson, Customer.class)

        from("amq:customers.delete")
                .unmarshal().json(JsonLibrary.Jackson, Customer.class)
                .bean(customerService, "deleteCustomer")

        from("amq:customers.authenticate")
                .unmarshal().json(JsonLibrary.Jackson, Customer.class)
                .bean(customerService, "authenticate")
                .marshal().json(JsonLibrary.Jackson, Customer.class)

        from("amq:customers.orders.get")
                .bean(customerService, 'getOrder(${header.orderId})')
                .marshal().json(JsonLibrary.Jackson, Order.class)

        from("amq:customers.orders.list")
                .bean(customerService, 'listOrders(${header.customerId})')
                .marshal().json(JsonLibrary.Jackson)

        from("amq:customers.orders.save")
                .unmarshal().json(JsonLibrary.Jackson, Order.class)
                .bean(customerService, "saveOrder")
                .marshal().json(JsonLibrary.Jackson, Order.class)

        from("amq:customers.orders.delete")
                .unmarshal().json(JsonLibrary.Jackson, Order.class)
                .bean(customerService, "deleteOrder")

        from("amq:customers.orders.orderItems.get")
                .bean(customerService, 'getOrderItem(${header.orderItemId})')
                .marshal().json(JsonLibrary.Jackson, OrderItem.class)

        from("amq:customers.orders.orderItems.getAll")
                .bean(customerService, 'listOrderItems(${header.orderId})')
                .marshal().json(JsonLibrary.Jackson)

        from("amq:customers.orders.orderItems.save")
                .bean(customerService, 'saveOrderItem(${header.orderId}, ${body})')
                .marshal().json(JsonLibrary.Jackson, OrderItem.class)

        from("amq:customers.orders.orderItems.delete")
                .unmarshal().json(JsonLibrary.Jackson, OrderItem.class)
                .bean(customerService, "deleteOrderItem")
    }
}