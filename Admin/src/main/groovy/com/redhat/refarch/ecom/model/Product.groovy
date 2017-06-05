package com.redhat.refarch.ecom.model

import groovy.transform.EqualsAndHashCode
import org.springframework.data.annotation.Id

import java.math.RoundingMode

@EqualsAndHashCode
class Product {

    @Id
    String sku
    String name
    String description
    Double length
    Double width
    Double height
    Double weight
    Boolean isFeatured
    Integer availability
    BigDecimal price
    String image
    List<String> keywords = []

    Product(String sku, String name, String description, Double length, Double width, Double height, Double weight,
            Boolean isFeatured, Integer availability, BigDecimal price, String image, List<String> keywords) {
        this.sku = sku
        this.name = name
        this.description = description
        this.length = length
        this.width = width
        this.height = height
        this.weight = weight
        this.isFeatured = isFeatured
        this.availability = availability
        this.price = price
        this.image = image
        this.keywords = keywords
    }

    @Override
    String toString() {
        return "Product{sku='${sku}', name='${name}', description='${description}', length=${length}, " +
                "width=${width}, height=${height}, weight=${weight}, isFeatured=${isFeatured}, " +
                "availability=${availability}, price=${price}, image='${image}', keywords=${keywords}}"
    }


    static class ProductBuilder {

        String sku
        String name
        String description
        Double length
        Double width
        Double height
        Double weight
        Boolean isFeatured
        Integer availability
        BigDecimal price
        String image
        List<String> keywords = []
        
        ProductBuilder sku(String sku) {
            this.sku = sku
            return this
        }

        ProductBuilder name(String name) {
            this.name = name
            return this
        }

        ProductBuilder description(String description) {
            this.description = description
            return this
        }

        ProductBuilder length(Double length) {
            this.length = length
            return this
        }

        ProductBuilder width(Double width) {
            this.width = width
            return this
        }

        ProductBuilder height(Double height) {
            this.height = height
            return this
        }

        ProductBuilder weight(Double weight) {
            this.weight = weight
            return this
        }

        ProductBuilder featured(Boolean featured) {
            this.isFeatured = featured
            return this
        }

        ProductBuilder availability(Integer availability) {
            this.availability = availability
            return this
        }

        ProductBuilder price(BigDecimal price) {
            this.price = price.setScale(2, RoundingMode.CEILING)
            return this
        }

        ProductBuilder image(String image) {
            this.image = image
            return this
        }

        ProductBuilder keywords(List<String> keywords) {
            this.keywords = keywords
            return this
        }

        Product build() {
            return new Product(sku, name, description, length, width, height, weight, isFeatured, availability, price,
            image, keywords)
        }
    }
}