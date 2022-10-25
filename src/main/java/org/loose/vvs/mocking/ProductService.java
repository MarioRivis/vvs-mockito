package org.loose.vvs.mocking;

public class ProductService {

    private ProductProvider productProvider;

    public double computeAveragePricesOfPhones() {
        return productProvider.searchForProduct("phone").stream()
                .mapToInt(Product::getPrice)
                .average()
                .orElse(0.);
    }

    

    public void setProductProvider(ProductProvider productProvider) {
        this.productProvider = productProvider;
    }
}
