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
import com.redhat.refarch.ecom.model.Product
import com.redhat.refarch.ecom.repository.CustomerRepository
import com.redhat.refarch.ecom.repository.OrderItemRepository
import com.redhat.refarch.ecom.repository.OrderRepository
import com.redhat.refarch.ecom.repository.ProductRepository
import org.apache.camel.Consume
import org.apache.http.HttpStatus
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPatch
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
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

        resetData()

        Gson gson = new Gson()

        Customer customer = new Customer("Bob Dole", "123 Somewhere St", "1234567890", "bob@dole.com", "bobdole",
                "password")

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
        customer =  gson.fromJson(EntityUtils.toString(httpClient.execute(get).getEntity()), Customer.class)
        Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
        Assert.assertNotNull(customer)
        Assert.assertEquals(customer, fetchedCustomer)

        // authenticate customer
        uriBuilder = getUriBuilder("customers", "authenticate")
        HttpPost post = new HttpPost(uriBuilder.build())
        post.setEntity(new StringEntity(gson.toJson(customer).toString(), ContentType.APPLICATION_JSON))
        customer = gson.fromJson(EntityUtils.toString(httpClient.execute(post).getEntity()), Customer.class)
        Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
        Assert.assertNotNull(customer)
        Assert.assertEquals(customer, fetchedCustomer)

        // delete customer
        uriBuilder = getUriBuilder("customers", customer.getId())
        HttpDelete delete = new HttpDelete(uriBuilder.build())
        httpClient.execute(delete)
        Assert.assertNull(customerRepository.getByUsername("bobdole"))

        // patch customer
        uriBuilder = getUriBuilder("customers")
        HttpPatch patch = new HttpPatch(uriBuilder.build())
        patch.setEntity(new StringEntity(gson.toJson(customer).toString(), ContentType.APPLICATION_JSON))
        customer = gson.fromJson(EntityUtils.toString(httpClient.execute(patch).getEntity()), Customer.class)
        fetchedCustomer = customerRepository.getByUsername("bobdole")
        Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
        Assert.assertNotNull(customer)
        Assert.assertEquals(customer, fetchedCustomer)

        // list featured products
        uriBuilder = getUriBuilder("products")
        get = new HttpGet(uriBuilder.build())
        List<Product> products = Arrays.asList(
                gson.fromJson(EntityUtils.toString(httpClient.execute(patch).getEntity()), Product[].class))
        List<Product> fetchedProducts = productRepository.findByIsFeatured(true)
        Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
        Assert.assertNotNull(products)
        Assert.assertTrue(products.size() > 0)
        Assert.assertEquals(products, fetchedProducts)

        // list products by keyword
        uriBuilder = getUriBuilder("products", "keywords", "Electronics")
        get = new HttpGet(uriBuilder.build())
        products = Arrays.asList(gson.fromJson(EntityUtils.toString(httpClient.execute(get).getEntity()), Product[].class))
        fetchedProducts = productRepository.findByKeywords("Electronics")
        Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
        Assert.assertNotNull(products)
        Assert.assertTrue(products.size() > 0)
        Assert.assertEquals(products, fetchedProducts)
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