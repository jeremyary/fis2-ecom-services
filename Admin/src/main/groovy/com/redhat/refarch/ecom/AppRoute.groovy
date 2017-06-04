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

import com.redhat.refarch.ecom.service.ProductService
import org.apache.camel.spring.SpringRouteBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AppRoute extends SpringRouteBuilder {

    @Autowired
    ProductService productService

    @Override
    void configure() throws Exception {

        from("amq:product.add")
                .bean(productService, "addProduct")

        from("amq:product.featured")
                .bean(productService, "findByIsFeatured")

        from("amq:product.get")
                .bean(productService, "getProduct")

        from("amq:product.save")
                .bean(productService, "saveProduct")

        from("amq:product.delete")
                .bean(productService, "deleteProduct")

        from("amq:product.addKeywords")
                .bean(productService, "addKeywordsToProduct")

        from("amq:product.getByKeyword")
                .bean(productService, "getProductsByKeyword")

        from("amq:product.reduce")
                .bean(productService, "reduceInventory")

    }
}