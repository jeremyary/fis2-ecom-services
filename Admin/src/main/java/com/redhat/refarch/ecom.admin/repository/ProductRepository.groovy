package com.redhat.refarch.ecom.admin.repository

import com.redhat.refarch.ecom.admin.model.Product
import org.springframework.data.mongodb.repository.MongoRepository

/***
 * @author jary@redhat.com
 */
interface ProductRepository extends MongoRepository<Product, String> {

    Product getBySku(String sku)

    List<Product> findByKeywords(String keyword)

    List<Product> findByIsFeatured(Boolean isFeatured)
}
