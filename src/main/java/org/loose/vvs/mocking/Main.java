package org.loose.vvs.mocking;

public class Main {
    public static void main(String[] args) {
        ProductService productService = new ProductService();
        productService.setProductProvider(new ProductProviderImpl());

        System.out.println(productService.computeAveragePricesOfPhones());
    }
}