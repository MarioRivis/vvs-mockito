package org.loose.vvs.mocking;

import java.util.List;

public interface ProductProvider {
    List<Product> getProducts();
    List<Product> searchForProduct(String query);
}
