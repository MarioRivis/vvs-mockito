package org.loose.vvs.mocking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private static final List<Product> someProducts = Arrays.asList(
            Product.builder().id(1).price(100).build(),
            Product.builder().id(2).price(300).build()
    );

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService();
    }

    @Test
    void testAveragePriceForPhonesWithNoProducts() {
        productService.setProductProvider(new ProductProvider() {
            @Override
            public List<Product> getProducts() {
                return new ArrayList<>();
            }

            @Override
            public List<Product> searchForProduct(String query) {
                return new ArrayList<>();
            }
        });

        assertEquals(0, productService.computeAveragePricesOfPhones());
    }

    @Test
    void testAveragePriceForPhonesWithNoProductsWithMock(@Mock ProductProvider productProvider) {
        productService.setProductProvider(productProvider);

        assertEquals(0, productService.computeAveragePricesOfPhones());
    }

    @Test
    void testAveragePriceForPhones() {
        productService.setProductProvider(new MockProductProvider());

        assertEquals(200, productService.computeAveragePricesOfPhones());
    }

    @Test
    void testAveragePriceWithMockito(@Mock ProductProvider productProvider) {
        when(productProvider.searchForProduct(anyString())).thenReturn(someProducts);
        productService.setProductProvider(productProvider);
        assertEquals(200, productService.computeAveragePricesOfPhones());
        verify(productProvider, atLeastOnce()).searchForProduct("phone");
    }

    private static class MockProductProvider implements ProductProvider {

        @Override
        public List<Product> getProducts() {
            return someProducts;
        }

        @Override
        public List<Product> searchForProduct(String query) {
            return someProducts;
        }
    }
}