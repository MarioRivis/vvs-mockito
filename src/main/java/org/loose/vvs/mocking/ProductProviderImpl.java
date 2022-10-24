package org.loose.vvs.mocking;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class ProductProviderImpl implements ProductProvider{

    private static final String PRODUCTS_URL = "https://dummyjson.com/products";
    private static final String PRODUCTS_SEARCH_URL = "https://dummyjson.com/products?q=";

    @Override
    public List<Product> getProducts() {

        try {
            String response = HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI.create(PRODUCTS_URL)).build(), HttpResponse.BodyHandlers.ofString()).body();

            return new ObjectMapper().readValue(response, ProductsResponse.class).getProducts();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Product> searchForProduct(String query) {
        try {
            String response = HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI.create(PRODUCTS_SEARCH_URL + query)).build(), HttpResponse.BodyHandlers.ofString()).body();

            return new ObjectMapper().readValue(response, ProductsResponse.class).getProducts();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
