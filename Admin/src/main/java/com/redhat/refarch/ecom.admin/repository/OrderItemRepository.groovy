package com.redhat.refarch.ecom.admin.repository

import com.redhat.refarch.ecom.admin.model.OrderItem
import org.springframework.data.mongodb.repository.MongoRepository

/***
 * @author jary@redhat.com
 */
interface OrderItemRepository extends MongoRepository<OrderItem, String> {

    List<OrderItem> findByIdIn(List<String> orderItemIds)

    OrderItem getById(String id)
}
