package com.redhat.refarch.ecom.service

import com.redhat.refarch.ecom.model.Error
import com.redhat.refarch.ecom.model.Inventory
import com.redhat.refarch.ecom.model.Product
import com.redhat.refarch.ecom.repository.ProductRepository
import org.apache.camel.Consume
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.core.Response

@Component
class ProductService {

    @Autowired
    ProductRepository productRepository

    Product getProduct(String sku) {
        return productRepository.getBySku(sku)
    }

    List<Product> getProductsByKeyword(String keyword) {
        return productRepository.findByKeywords(keyword)
    }

    List<Product> findFeatured() {
        return productRepository.findByIsFeatured(true)
    }

    Product saveProduct(Product product) {
        return productRepository.save(product)
    }

    Response deleteProduct(String sku) {
        productRepository.delete(sku)
        return Response.ok().build()
    }

    Response reduceInventory(Inventory[] inventoryAdjustment) {
        try {
            for (Inventory inventory : inventoryAdjustment) {

                Product product = getProduct(inventory.sku)
                if (product == null) {
                    throw new Error(HttpURLConnection.HTTP_NOT_FOUND, "Product not found").asException()
                }
                if (inventory.getQuantity() > product.getAvailability()) {
                    String message = "Insufficient availability for " + inventory.getSku()
                    throw new Error(HttpURLConnection.HTTP_CONFLICT, message).asException()
                } else {
                    product.setAvailability(product.getAvailability() - inventory.getQuantity())
                    productRepository.save(product)
                }
            }
        }
        catch (RuntimeException e) {
            throw new Error(HttpURLConnection.HTTP_INTERNAL_ERROR, e).asException()
        }
        return Response.ok().build()
    }

    Response addKeywordsToProduct(String sku, List<String> keywords) {

        Product product = getProduct(sku)
        product.setKeywords(keywords)
        saveProduct(product)
        return Response.ok().build()
    }
}