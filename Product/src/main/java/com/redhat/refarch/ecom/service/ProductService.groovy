package com.redhat.refarch.ecom.service

import com.redhat.refarch.ecom.model.Error
import com.redhat.refarch.ecom.model.Product
import com.redhat.refarch.ecom.repository.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

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

    void deleteProduct(String sku) {
        productRepository.delete(sku)
    }

    void reduceInventory(String sku, Integer quantity) {

        Product product = getProduct(sku)
        if (product == null) {
            throw new Error(HttpURLConnection.HTTP_NOT_FOUND, "Product not found").asException()
        }
        if (quantity > product.getAvailability()) {
            String message = "Insufficient availability for ${sku}"
            throw new Error(HttpURLConnection.HTTP_CONFLICT, message).asException()
        } else {
            product.setAvailability(product.getAvailability() - quantity)
            productRepository.save(product)
        }
    }

    Product addKeywordsToProduct(String sku, String[] keywords) {

        Product product = getProduct(sku)
        product.setKeywords(Arrays.asList(keywords))
        saveProduct(product)
    }
}