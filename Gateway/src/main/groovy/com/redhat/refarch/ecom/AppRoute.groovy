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

import org.apache.camel.spring.SpringRouteBuilder
import org.springframework.stereotype.Component

@Component
class AppRoute extends SpringRouteBuilder {

    @Override
    void configure() throws Exception {

        restConfiguration().component("spark-rest").host("0.0.0.0").port(9091)

        rest("/billing/process")
                .post().route()
                .to("amq:billing.process?transferException=true")
                .wireTap("direct:warehouse")

        from("direct:warehouse")
                .routeId("warehouseMsgGateway")
                .filter(simple("${bodyAs(String)} contains 'SUCCESS'"))
                .inOnly("amq:topic:warehouse.fulfill")

        rest("/billing/refund/{transactionNumber}")
                .post().to("amq:billing.refund?transferException=true")

        rest("/customers")
                .put().to("amq:customers.save?transferException=true")
                .patch().to("amq:customers.save?transferException=true")
                .delete().to("amq:customers.delete?transferException=true")

        rest("/customers/authenticate")
                .post().to("amq:customers.authenticate?transferException=true")

        rest("/customers/{customerId}")
                .get().to("amq:customers.get?transferException=true")

        rest("/customers/{customerId}/orders")
                .get().to("amq:customers.orders.list?transferException=true")
                .put().to("amq:customers.orders.save?transferException=true")
                .patch().to("amq:customers.orders.save?transferException=true")
                .delete().to("amq:customers.orders.delete?transferException=true")

        rest("/customers/{customerId}/orders/{orderId}")
                .get().to("amq:customers.orders.get?transferException=true")

        rest("/customers/{customerId}/orders/{orderId}/orderItems")
                .get().to("amq:customers.orders.orderItems.list?transferException=true")
                .put().to("amq:customers.orders.orderItems.save?transferException=true")
                .patch().to("amq:customers.orders.orderItems.save?transferException=true")
                .delete().to("amq:customers.orders.orderItems.delete?transferException=true")

        rest("/customers/{customerId}/orders/{orderId}/orderItems/{orderItemId}")
                .get().to("amq:customers.orders.orderItems.get?transferException=true")

        rest("/products")
                .get().to("amq:products.list.featured?transferException=true")
                .put().to("amq:products.save?transferException=true")
                .patch().to("amq:products.save?transferException=true")
                .delete().to("amq:products.delete?transferException=true")

        rest("/products/{sku}")
                .get().to("amq:products.get?transferException=true")

        rest("/products/{sku}/classify")
                .post().to("amq:products.delete?transferException=true")

        rest("/products/keywords")
                .post().to("amq:products.delete?transferException=true")

        rest("/products/keywords/{keyword}")
                .get().to("amq:products.list.keyword?transferException=true")

        rest("admin/reset")
                .get().to("amq:admin.reset?transferException=true")

        rest("admin/testApi")
                .get().to("amq:admin.testApi?transferException=true")
    }
}