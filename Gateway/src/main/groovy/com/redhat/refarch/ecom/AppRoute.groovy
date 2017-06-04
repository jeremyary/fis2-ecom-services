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

import com.redhat.refarch.ecom.service.CustomerService
import org.apache.camel.spring.SpringRouteBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AppRoute extends SpringRouteBuilder {

    @Autowired
    CustomerService customerService

    @Override
    void configure() throws Exception {

        from("amq:customers.authenticate")
                .bean(customerService, "authenticate")

        from("amq:customers.get")
                .bean(customerService, "getCustomer")

        from("amq:customers.save")
                .bean(customerService, "saveCustomer")

        from("amq:customers.delete")
                .bean(customerService, "deleteCustomer")

        from("amq:customers.orders.getOne")
                .bean(customerService, "getOrder")

        from("amq:customers.orders.getAll")
                .bean(customerService, "getOrders")

        from("amq:customers.orders.save")
                .bean(customerService, "saveOrder")

        from("amq:customers.orders.delete")
                .bean(customerService, "deleteOrder")

        from("amq:customers.orderItems.getOne")
                .bean(customerService, "getOrderItem")

        from("amq:customers.orderItems.getAll")
                .bean(customerService, "getOrderItems")

        from("amq:customers.orderItems.saveOne")
                .bean(customerService, "saveOrderItemById")

        from("amq:customers.orderItems.saveAll")
                .bean(customerService, "saveOrderItem")

        from("amq:customers.orderItems.delete")
                .bean(customerService, "deleteOrderItem")
    }
}