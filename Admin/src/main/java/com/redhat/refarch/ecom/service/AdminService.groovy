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

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.redhat.refarch.ecom.model.Customer
import com.redhat.refarch.ecom.model.Order
import com.redhat.refarch.ecom.model.OrderItem
import com.redhat.refarch.ecom.model.Product
import com.redhat.refarch.ecom.repository.CustomerRepository
import com.redhat.refarch.ecom.repository.OrderItemRepository
import com.redhat.refarch.ecom.repository.OrderRepository
import com.redhat.refarch.ecom.repository.ProductRepository
import org.apache.camel.Consume
import org.apache.http.HttpStatus
import org.apache.http.client.methods.*
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.hamcrest.collection.IsIterableContainingInAnyOrder
import org.hamcrest.collection.IsIterableContainingInOrder
import org.junit.Assert
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

        try {
            customerRepository.deleteAll()
            productRepository.deleteAll()
            orderRepository.deleteAll()
            orderItemRepository.deleteAll()

            JsonReader jsonReader = new JsonReader(new InputStreamReader(AdminService.class.getResourceAsStream
                    ("/product_filler.json")))

            productRepository.save(Arrays.asList(new Gson().fromJson(jsonReader, Product[].class)))

        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    @Consume(uri = "amq:admin.testApi")
    void testApi() {

        try {
            resetData()

            Gson gson = new Gson()

            Customer customer = new Customer()
            customer.setName("Bob Dole")
            customer.setAddress("123 Somewhere St")
            customer.setTelephone("1234567890")
            customer.setEmail("bob@dole.com")
            customer.setUsername("bobdole")
            customer.setPassword("password")

            // save new customer
            CloseableHttpClient httpClient = HttpClients.createDefault()
            URIBuilder uriBuilder = getUriBuilder("customers")
            HttpPut put = new HttpPut(uriBuilder.build())
            put.setEntity(new StringEntity(gson.toJson(customer).toString(), ContentType.APPLICATION_JSON))
            CloseableHttpResponse response = httpClient.execute(put)
            Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            Customer fetchedCustomer = customerRepository.getByUsername("bobdole")
            Assert.assertNotNull(fetchedCustomer)

            // get customer
            uriBuilder = getUriBuilder("customers", fetchedCustomer.id)
            HttpGet get = new HttpGet(uriBuilder.build())
            customer = gson.fromJson(EntityUtils.toString(httpClient.execute(get).getEntity()), Customer.class)
            Assert.assertNotNull(customer)
            Assert.assertEquals(customer, fetchedCustomer)

            // authenticate customer
            uriBuilder = getUriBuilder("customers", "authenticate")
            HttpPost post = new HttpPost(uriBuilder.build())
            post.setEntity(new StringEntity(gson.toJson(customer).toString(), ContentType.APPLICATION_JSON))
            customer = gson.fromJson(EntityUtils.toString(httpClient.execute(post).getEntity()), Customer.class)
            Assert.assertNotNull(customer)
            Assert.assertEquals(customer, fetchedCustomer)

            // delete customer
            uriBuilder = getUriBuilder("customers", customer.getId())
            HttpDelete delete = new HttpDelete(uriBuilder.build())
            response = httpClient.execute(delete)
            EntityUtils.consumeQuietly(response.getEntity())
            Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            Assert.assertNull(customerRepository.getByUsername("bobdole"))

            // patch customer
            uriBuilder = getUriBuilder("customers")
            HttpPatch patch = new HttpPatch(uriBuilder.build())
            patch.setEntity(new StringEntity(gson.toJson(customer).toString(), ContentType.APPLICATION_JSON))
            customer = gson.fromJson(EntityUtils.toString(httpClient.execute(patch).getEntity()), Customer.class)
            fetchedCustomer = customerRepository.getByUsername("bobdole")
            Assert.assertNotNull(customer)
            Assert.assertEquals(customer, fetchedCustomer)

            // add product
            Product newProduct = new Product.ProductBuilder()
                    .name("Fancy TV")
                    .description("Fancy television")
                    .length(80.2)
                    .width(1.5)
                    .height(40.3)
                    .weight(5.5)
                    .featured(true)
                    .availability(5)
                    .price(99.99)
                    .image("TV")
                    .build()
            uriBuilder = getUriBuilder("products")
            put = new HttpPut(uriBuilder.build())
            put.setEntity(new StringEntity(gson.toJson(newProduct).toString(), ContentType.APPLICATION_JSON))
            Product product = gson.fromJson(EntityUtils.toString(httpClient.execute(put).getEntity()), Product.class)
            Assert.assertNotNull(product)
            newProduct.setSku(product.sku)
            Assert.assertEquals(product, newProduct)

            // update product
            product.setDescription("foo")
            uriBuilder = getUriBuilder("products")
            patch = new HttpPatch(uriBuilder.build())
            patch.setEntity(new StringEntity(gson.toJson(product).toString(), ContentType.APPLICATION_JSON))
            product = gson.fromJson(EntityUtils.toString(httpClient.execute(patch).getEntity()), Product.class)
            Assert.assertNotNull(product)
            Assert.assertTrue(product.description == "foo")
            Product fetchedProduct = productRepository.findOne(product.sku)
            Assert.assertEquals(product, fetchedProduct)

            // add keywords to product
            uriBuilder = getUriBuilder("products", product.sku)
            post = new HttpPost(uriBuilder.build())
            String[] keywords = ["Electronics", "TV"]
            post.setEntity(new StringEntity(gson.toJson(keywords).toString(), ContentType.APPLICATION_JSON))
            response = httpClient.execute(post)
            EntityUtils.consumeQuietly(response.getEntity())
            Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            product = productRepository.findOne(product.sku)
            Assert.assertTrue(product.getKeywords().containsAll(keywords))

            // reduce product
            uriBuilder = getUriBuilder("products", product.sku, "reduce", "1")
            get = new HttpGet(uriBuilder.build())
            response = httpClient.execute(get)
            EntityUtils.consumeQuietly(response.getEntity())
            Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            product = productRepository.findOne(product.sku)
            Assert.assertTrue(product.getAvailability() == 4)

            // delete product
            uriBuilder = getUriBuilder("products", product.sku)
            delete = new HttpDelete(uriBuilder.build())
            response = httpClient.execute(delete)
            EntityUtils.consumeQuietly(response.getEntity())
            Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            Assert.assertNull(orderRepository.findOne(product.sku))

            // list featured products
            uriBuilder = getUriBuilder("products")
            get = new HttpGet(uriBuilder.build())
            List<Product> products = Arrays.asList(
                    gson.fromJson(EntityUtils.toString(httpClient.execute(get).getEntity()), Product[].class))
            List<Product> fetchedProducts = productRepository.findByIsFeatured(true)
            Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            Assert.assertNotNull(products)
            Assert.assertTrue(products.size() > 0)
            Assert.assertThat(products, IsIterableContainingInOrder.contains(fetchedProducts.toArray()))

            // list products by keyword
            uriBuilder = getUriBuilder("products", "keywords", "Electronics")
            get = new HttpGet(uriBuilder.build())
            products = Arrays.asList(gson.fromJson(EntityUtils.toString(httpClient.execute(get).getEntity()), Product[].class))
            fetchedProducts = productRepository.findByKeywords("Electronics")
            Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            Assert.assertNotNull(products)
            Assert.assertTrue(products.size() > 0)
            Assert.assertThat(products, IsIterableContainingInAnyOrder.containsInAnyOrder(fetchedProducts.toArray()))

            // get product to check availability
            fetchedProduct = fetchedProducts.get(0)
            uriBuilder = getUriBuilder("products", fetchedProduct.getSku())
            get = new HttpGet(uriBuilder.build())
            product = gson.fromJson(EntityUtils.toString(httpClient.execute(get).getEntity()), Product.class)
            Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            Assert.assertNotNull(products)
            Assert.assertEquals(product, fetchedProduct)

            // add initial order
            Order newOrder = new Order()
            newOrder.setStatus(Order.Status.Initial)
            uriBuilder = getUriBuilder("customers", customer.id, "orders")
            post = new HttpPost(uriBuilder.build())
            post.setEntity(new StringEntity(gson.toJson(newOrder).toString(), ContentType.APPLICATION_JSON))
            Order order = gson.fromJson(EntityUtils.toString(httpClient.execute(post).getEntity()), Order.class)
            Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            Assert.assertNotNull(order)
            Order fetchedOrder = orderRepository.findOne(order.id)
            Assert.assertEquals(order, fetchedOrder)

            // add order item
            OrderItem newOrderItem = new OrderItem()
            newOrderItem.setSku(product.sku)
            newOrderItem.setQuantity(1)
            uriBuilder = getUriBuilder("customers", customer.id, "orders", order.id, "orderItems")
            post = new HttpPost(uriBuilder.build())
            post.setEntity(new StringEntity(gson.toJson(newOrderItem).toString(), ContentType.APPLICATION_JSON))
            OrderItem orderItem = gson.fromJson(EntityUtils.toString(httpClient.execute(post).getEntity()), OrderItem
                    .class)
            Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            Assert.assertNotNull(orderItem)
            OrderItem fetchedOrderItem = orderItemRepository.findOne(orderItem.id)
            Assert.assertEquals(orderItem, fetchedOrderItem)
            fetchedOrder = orderRepository.findOne(fetchedOrder.id)
            Assert.assertTrue(fetchedOrder.orderItemIds.size() == 1)
            Assert.assertTrue(fetchedOrder.orderItemIds.contains(fetchedOrderItem.id))

            // update order item
            newOrderItem.setQuantity(3)
            uriBuilder = getUriBuilder("customers", customer.id, "orders", order.id, "orderItems")
            patch = new HttpPatch(uriBuilder.build())
            patch.setEntity(new StringEntity(gson.toJson(newOrderItem).toString(), ContentType.APPLICATION_JSON))
            orderItem = gson.fromJson(EntityUtils.toString(httpClient.execute(patch).getEntity()), OrderItem.class)
            Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            Assert.assertNotNull(orderItem)
            fetchedOrderItem = orderItemRepository.findOne(orderItem.id)
            Assert.assertEquals(orderItem, fetchedOrderItem)
            Assert.assertTrue(fetchedOrderItem.quantity == 3)
            fetchedOrder = orderRepository.findOne(fetchedOrder.id)
            Assert.assertTrue(fetchedOrder.orderItemIds.size() == 1)
            Assert.assertTrue(fetchedOrder.orderItemIds.contains(fetchedOrderItem.id))


        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    private static URIBuilder getUriBuilder(Object... path) {

        URIBuilder uriBuilder = new URIBuilder()
                .setScheme("http")
                .setHost("ecom.rhmap.ose")

        StringWriter stringWriter = new StringWriter()
        path.each {
            stringWriter.append("/${String.valueOf(it)}")
        }
        uriBuilder.setPath(stringWriter.toString())
        return uriBuilder
    }
}