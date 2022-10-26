# Lab 3 VVS - Mock Unit Testing 

## Introduction

In this laboratory we will learn how to unit test our code using mock object. Additionally, we will learn how to use the [Mockito](https://site.mockito.org/) framework to create mock objects.

## Mocks

Before we start, let's define what a mock is. A mock is an object that simulates the behavior of a real object in controlled ways. A mock object is used in unit testing to isolate the object under test from other objects. The mocks are used to test the behavior of a class that has dependencies on other classes. The idea is not to use the real implementation that can be slow, hard to set up, or not even available, but instead use a simplified implementation that can be controlled by the test. The mocks only use input and output parameters to simulate the behavior of the real object. *The unit tests must be focused on functionality of the tested class, not on the other clases that are used by it.*


## Mockito

[Mockito](https://site.mockito.org/) is a Java framework that offers the possibility to create mocks. Additionaly, it offers the possibility to verify the behavior of the mock objects.

## Example

To better understand the different metods to mock a class, we will use a concrete exemple. A functional exemple can be found at the following [link](https://github.com/MarioRivis/vvs-mockito).


### The implementation of the ProductService class

We have an interface called `ProductProvider` that offers two methods to obtain products. The method `getProducts` returns a list with all the products and the method `searchForProduct` returns all the products from a given category.
 

```java
import java.util.List;

public interface ProductProvider {
    List<Product> getProducts();
    List<Product> searchForProduct(String query);
}
```

The class `ProductService` has a method called `computeAveragePricesOfPhones` that computes the average price of phones. To obtain the prices of the phones, the method `searchForProduct` from the interface `ProductProvider` is used. The concrete implementation of the interface `ProductProvider` is the class `ProductProviderImpl` that uses an external API to obtain the products.

```java
public class ProductService {

    private ProductProvider productProvider;

    public void setProductProvider(ProductProvider productProvider) {
        this.productProvider = productProvider;
    }

    public double computeAveragePricesOfPhones() {
        return productProvider.searchForProduct("phone").stream()
                .mapToInt(Product::getPrice)
                .average()
                .orElse(0.);
    }
}
```

> Before using the method `computeAveragePricesOfPhones`, the method `setProductProvider` must be called to set a concrete implementation of the interface `ProductProvider`.


### The testing of the ProductService class

The class `ProductService` depends on an inteface `ProductProvider`. To test the class `ProductService`, we must use a concrete implementation of the interface `ProductProvider`. The problem is that we cannot use the concrete implementation of the interface `ProductProvider` because it uses an external API. This makes the testing not repeatable. (Running the tests will require access to the internet or there may be connection problems or the external API may be unavailable or even change the returned data).

For this reason, we replace the concrete implementation of the interface `ProductProvider` with a mock. In the following, three types of mocks will be presented, but the methods presented are not the only options to use mocks.

#### 1. Inline / Anonymous class

The following test uses an anonymous class to create a mock. All the methods from the interface `ProductProvider` must be implemented with concrete values for the test case. In our case, the test verifies the situation when there are no products in the list of products, so an empty list is returned.

```java
class ProductServiceTest {
    @Test
    void testAveragePriceForPhonesWithNoProducts() {
        ProductService productService = new ProductService();

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
}
```

#### 2. Seperate class

Similar to the anonymous class, the following test uses a separate class to create a mock. In our case, the test verifies the situation when there is only one product in the list of products.

The mock must implement all the methods from the interface `ProductProvider`. Since we do not use the method `getProducts` in the test case presented, we can implement it as we want.

```java

class ProductServiceTest {
    @Test
    void testAveragePriceForPhones() {
        ProductService productService = new ProductService();

        productService.setProductProvider(new MockProductProvider());

        assertEquals(200, productService.computeAveragePricesOfPhones());
    }

    private static class MockProductProvider implements ProductProvider {

        @Override
        public List<Product> getProducts() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Product> searchForProduct(String query) {
            return new ArrayList<>(Collections.singletonList(Product.builder().id(1).price(200).build()));
        }
    }
}
```
---

#### 3. Mockito

In the following section, we will present a test that uses Mockito. In our case, the test verifies the situation when there are more products in the list of products.

The dezadvantage of the previous methods in which anonymous or separate classes are created is the need to implement the methods that are not used in the test. Over time, various classes are created for different test cases, and the code can become difficult to follow and maintain.

The Mockito framework offers a simpler method to create mocks in which it is not necessary to implement the methods that are not used in the test.

#### Dependencies

To use Mockito, we must add the dependency in `pom.xml`:

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>4.6.1</version>
    <scope>test</scope>
</dependency>
```

Mockito integrates with Junit5 using the dependency:

```xml 
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>4.6.1</version>
    <scope>test</scope>
</dependency>
```

#### The declaration of the mock

In order for Mockito to be able to create mocks in JUnit5, an annotation must be added above the test class.

```java
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
}
```

To create a mock using Mockito, the annotation `@Mock` must be added above the variable that is to be mocked. The annotation works both for parameters of test methods and for fields of the test class. Any of these options can be used, but it is recommended to use consistently in all test cases.

```java
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductProvider productProviderField;

    @Test
    void testAveragePriceWithMockito(@Mock ProductProvider productProviderParameter) {    
    }
}
```

Up until this point, we have initialized the project and the test class and we have presented 2 ways to create mocks. In the following, we will present how to use the mocks to test the method `computeAveragePricesOfPhones`.

To replace the implementation of the method `searchForProduct` from `ProductProvider`, we will use the construction `when .. thenReturn`. This construction is interpreted as follows: when productService (the tested class) will call the function `searchForProduct` with the parameter `"phone"`, then a list with a single product will be returned.


```java
when(productProvider.searchForProduct("phone"))
    .thenReturn(new ArrayList<>(Collections.singletonList(
            Product.builder().id(1).price(200).build())));
```

This construction replaces the creation of anonymous or separate classes presented above, and the test is more readable.

The test that verifies the situation when there are more products in the list of products looks like this:

```java
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    private static final List<Product> products = new ArrayList<>(Arrays.asList(
        Product.builder().id(1).price(100).build(),
        Product.builder().id(2).price(300).build()
    );


    @Test
    void testAveragePriceWithMockito(@Mock ProductProvider productProvider) {) {
        ProductService productService = new ProductService();
        productService.setProductProvider(productProvider);

        when(productProvider.searchForProduct("phone")).thenReturn(products);

        assertEquals(250, productService.computeAveragePricesOfPhones());
    }
}
```

The `when .. thenReturn` construction can be used multiple times in a test case to capture different situations. Mockito saves the return values for each mock call based on the function names and their parameters.

For example, a mock call of the function `searchForProduct` with the parameter `"phone"` will return a list with a single product, and a mock call of the function `searchForProduct` with the parameter `"laptop"` will return a list with 2 products. Depending on the concrete implementation of the class that calls `searchForProduct`, different results will be returned.

```java
when(productProviderMock.searchForProduct("phone")).thenReturn(new ArrayList<>(Collections.singletonList(
        Product.builder().id(1).category("phone").price(200).build()
)));
when(productProviderMock.searchForProduct("laptop")).thenReturn(new ArrayList<>(Arrays.asList(
    Product.builder().id(1).price(100).category("laptop").build(),
    Product.builder().id(1).price(300).category("laptop").build()
)));
```

> Obersevation: For functions that do not return anything (void), `when .. thenReturn` is not used

In order for a mocked function to throw an exception, the following constructions are used:


For functions that return something, not void

```java
when(productProviderMock.searchForProduct("phone")).thenThrow(new RuntimeException("Error"));
when(productProviderMock.searchForProduct("phone")).thenThrow(NullPointerException.class);
```

For void functions

```java
doThrow(new RuntimeException("Error")).when(loggerMock).log(anyString());
doThrow(IllegalStateException.class).when(loggerMock).log(anyString());
```

--- 

#### Argument Matchers

In some cases, we are not interested in the value of the parameters of the mocked methods, but only in the fact that these have been called. In this case, Mockito offers the possibility to use argument matchers. Argument matchers are used to verify whether a certain parameter has been called with a certain value or not. Argument matchers are used in combination with `when .. thenReturn` and `verify`.

```java
when(productProvider.searchForProduct(anyString())).thenReturn(products);
```

Some examples are:
- `anyString()`
- `anyInt()`
- `anyDouble()`
- `anyBoolean()`
- `any()`
- `any(Example.class)` - matches the parameter to any object of the class `Example`
- `anyList()`
- `eq("valoare")` - matches the parameter to the value `"valoare"`

The complete list of matchers can be found [here](https://javadoc.io/static/org.mockito/mockito-core/3.3.3/org/mockito/ArgumentMatchers.html)

> Observation: If at least one argument matcher is used, then all the parameters must be matched with argument matchers.


```java
when(service.search("value", 2).thenReturn(products); // Ok
when(service.search(anyString(), anyInt()).thenReturn(products); // Ok
when(service.search(eq("value"), anyInt()).thenReturn(products); // Ok
when(service.search(anyString(), eq(2)).thenReturn(products); // Ok
when(service.search(eq("value"), eq(2)).thenReturn(products); // Ok
when(service.search("value", anyInt()).thenReturn(products); // Not ok, will throw error
```

---

#### Verifying the calls of mocked methods

In the case when we want to verify that a method has been called, we use `verify`. `verify` can be used in combination with `when .. thenReturn` or without it.


```java
when(productProvider.searchForProduct("phone")).thenReturn(products);

productService.computeAveragePricesOfPhones();

verify(productProvider).searchForProduct("phone");
```

In the case when we want to verify that a method has been called a certain number of times, we use `verify` in combination with `times`.


```java
when(productProvider.searchForProduct("phone")).thenReturn(products);

productService.computeAveragePricesOfPhones();
productService.computeAveragePricesOfPhones();

verify(productProvider, times(2)).searchForProduct("phone");
```

In the case when we want to verify that a method has been called with a certain parameter, but we are not interested in the value, we use `verify` in combination with `ArgumentMatchers`.

```java
when(productProvider.searchForProduct("phone")).thenReturn(products);

productService.computeAveragePricesOfPhones();

verify(productProvider).searchForProduct(anyString());
```

Some examples for the type of verification of the number of calls are:
- `times(2)` - verifies if the method has been called 2 times
- `atLeast(2)` - verifies if the method has been called at least 2 times
- `atLeastOnce()` - verifies if the method has been called at least once
- `atMost(2)` - verifies if the method has been called at most 2 times
- `never()` - verifies if the method has not been called
- `only()` - verifies if the method has been called only once

More information can be found at the following [link](https://site.mockito.org/javadoc/current/org/mockito/verification/class-use/VerificationMode.html)

The implementation with all the information presented above can be found in the example below:

```java
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
    void testAveragePriceWithMockito(@Mock ProductProvider productProvider) {
        when(productProvider.searchForProduct(anyString())).thenReturn(someProducts);

        productService.setProductProvider(productProvider);

        assertEquals(200, productService.computeAveragePricesOfPhones());
        verify(productProvider, atLeastOnce()).searchForProduct("phone");
        verify(productProvider, never()).getProducts();
    }
}
```

## Exercises

Start from the code found [here](https://github.com/MarioRivis/vvs-mockito), implement and test the following methods of the `ProductService` class:

- `double computeMinimumPriceOfAllProducts(String brand)`
- `double computeMaximumRatingOfAllProducts()`
- `Product findProductWithTheLongestTitleWithRating(double minRating, double maxRating)`


## Resurse

- [Mockito](https://site.mockito.org/)
- [Mockito Javadoc](https://javadoc.io/static/org.mockito/mockito-core/3.3.3/org/mockito/Mockito.html)
- [Mockito ArgumentMatchers](https://javadoc.io/static/org.mockito/mockito-core/3.3.3/org/mockito/ArgumentMatchers.html)
- [Mockito VerificationMode](https://site.mockito.org/javadoc/current/org/mockito/verification/class-use/VerificationMode.html)
- [Mockito Tutorial](https://www.vogella.com/tutorials/Mockito/article.html)
- [Mockito Tutorial](https://www.baeldung.com/mockito)
- [Mockito Tutorial](https://www.tutorialspoint.com/mockito/index.htm)
- [Mockito Tutorial](https://www.javatpoint.com/mockito-tutorial)
- [Mockito Tutorial](https://www.journaldev.com/21835/mockito-tutorial)