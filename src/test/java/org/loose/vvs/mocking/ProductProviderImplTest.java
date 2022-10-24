package org.loose.vvs.mocking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductProviderImplTest {

    private ProductProvider productProvider;

    @BeforeEach
    void setUp() {
        productProvider = new ProductProviderImpl();
    }

    @Test
    void getProducts() {
        assertNotNull(productProvider.getProducts());
    }

    @Test
    void searchForProduct() {
        assertNotNull(productProvider.searchForProduct("phone"));
    }
}