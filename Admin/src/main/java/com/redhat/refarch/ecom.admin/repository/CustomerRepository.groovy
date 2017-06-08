package com.redhat.refarch.ecom.admin.repository

import com.redhat.refarch.ecom.admin.model.Customer
import org.springframework.data.mongodb.repository.MongoRepository

/***
 * @author jary@redhat.com
 */
interface CustomerRepository extends MongoRepository<Customer, String> {

    Customer getByUsername(String username)

    Customer getById(String id)
}
