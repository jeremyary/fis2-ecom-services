/**
 *  Copyright 2005-2017 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License") you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package com.redhat.refarch.ecom.service

import com.redhat.refarch.ecom.model.Product
import com.redhat.refarch.ecom.repository.CustomerRepository
import com.redhat.refarch.ecom.repository.OrderItemRepository
import com.redhat.refarch.ecom.repository.OrderRepository
import com.redhat.refarch.ecom.repository.ProductRepository
import org.apache.camel.Consume
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AdminService {

    @Autowired
    CustomerRepository customerRepository

    @Autowired
    ProductRepository productRepository

    @Autowired
    OrderRepository orderRepository

    @Autowired
    OrderItemRepository orderItemRepository

    @Consume(uri = "amq:admin.reset")
    void resetData() {

        JSONParser parser = new JSONParser()
        try {

            customerRepository.deleteAll()
            productRepository.deleteAll()
            orderRepository.deleteAll()
            orderItemRepository.deleteAll()

            Object input = parser.parse(
                    new InputStreamReader(AdminService.class.getResourceAsStream("/product_filler.json")))

            List<Product> products = []
            ((JSONArray) input).each { obj ->

                obj = (JSONObject) obj
                products.add(new Product.ProductBuilder()
                        .sku((String) obj.get("sku"))
                        .name((String) obj.get("name"))
                        .description((String) obj.get("description"))
                        .length((Integer) obj.get("length"))
                        .width((Integer) obj.get("width"))
                        .height((Integer) obj.get("height"))
                        .weight((Integer) obj.get("weight"))
                        .featured((Boolean) obj.get("featured"))
                        .availability((Integer) obj.get("availability"))
                        .price((BigDecimal) obj.get("price"))
                        .image((String) obj.get("image"))
                        .keywords((JSONArray) obj.get("keywords"))
                        .build())
            }
            productRepository.save(products)

        } catch (Exception e) {
            e.printStackTrace()
        }
    }
}