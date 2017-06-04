package com.redhat.refarch.ecom.service

import com.redhat.refarch.ecom.model.Error
import com.redhat.refarch.ecom.model.Inventory
import com.redhat.refarch.ecom.model.Product
import com.redhat.refarch.ecom.repository.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.core.Response

@Component
class ProductService {

    @Autowired
    ProductRepository productRepository

    Product saveProduct(Product product) {
        return productRepository.save(product)
    }

    Product getProduct(String sku) {
        return productRepository.getBySku(sku)
    }

    void deleteProduct(String sku) {
        productRepository.delete(productRepository.getBySku(sku))
    }

    List<Product> getProductsByKeyword(String keyword) {
        return productRepository.findByKeywords(keyword)
    }

    List<Product> findByIsFeatured() {
        List<Product> products = productRepository.findByIsFeatured(true)
        println (products.toListString())
        return products
    }

    void addKeywordsToProduct(String sku, List<String> keywords) {

        Product product = getProduct(sku)
        product.setKeywords(keywords)
        saveProduct(product)
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
}