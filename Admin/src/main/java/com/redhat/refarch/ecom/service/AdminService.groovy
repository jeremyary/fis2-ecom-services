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
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
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

        CloseableHttpClient httpClient = HttpClients.createDefault()
        URIBuilder uriBuilder = getUriBuilder("customers")
        HttpPut put= new HttpPut(uriBuilder.build())
        put.setEntity(new StringEntity(gson.toJson(customer).toString(), ContentType.APPLICATION_JSON))
        CloseableHttpResponse response = httpClient.execute(put)

        Assert.assertFalse(response.getStatusLine().getStatusCode() >= HttpStatus.SC_BAD_REQUEST)
        Customer fetched = customerRepository.getByUsername("bobdole")
        Assert.assertNotNull(fetched)
        Assert.assertNotNull(fetched.getId() != null)
        Assert.assertTrue(fetched.getId().length() > 0)

        uriBuilder = getUriBuilder("customers", fetched.id)
        HttpGet get = new HttpGet(uriBuilder.build())
        customer = (Customer) httpClient.execute(get).getEntity()
        Assert.assertNotNull(customer)

//        uriBuilder = getUriBuilder("customers")
//        customer.setAddress("321 Nowhere St")
//        post = new HttpPost(uriBuilder.build())
//        post.setEntity(new StringEntity(gson.toJson(customer).toString(), ContentType.APPLICATION_JSON))
//        response = httpClient.execute(post)

        Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
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