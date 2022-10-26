# Lab 3 VVS - Mock Unit Testing 

## Introducere

În acest laborator vom învăța cum să folosim mock-uri pentru a testa unitățile de cod. În plus, vom învăța cum să folosim biblioteca [Mockito](https://site.mockito.org/) pentru a crea mock-uri.

## Mock-uri

Înainte de a trece la Mockito, vom vedea ce este un mock. Un mock este o instanță a unei clase care înlocuiește o altă instanță a aceleiași clase. Mock-urile sunt folosite pentru a simula comportamentul unei clase în testele unitare. În plus, mock-urile sunt folosite pentru a testa clase care depind de alte clase. Ideea este de a nu folosi implementarea concreta care poate fi foarte complexa si inutila, ci o implementare simpla care sa ne ajute sa testam functionalitatea clasei pe care o testam. Mock-urile se folosesc doar de valori de intrare si de iesire pentru a inlocui implementarea concreta. *Testele unitare trebuie sa se concentreze doar pe functionalitatea clasei pe care o testam, nu si pe alte clase care pot fi folosite de aceasta.* 


## Mockito

[Mockito](https://site.mockito.org/) este o bibliotecă pentru Java care oferă funcționalități pentru a crea mock-uri. În plus, Mockito oferă funcționalități pentru a verifica dacă un mock a fost folosit într-un test. 

## Exemplu

Pentru a intelege mai bine diferitele metode de a mock-ui o clasa, vom folosi un exemplu concret. Un exemplu functional se gaseste la urmatorul [link](https://github.com/MarioRivis/vvs-mockito).

### Implementarea clasei ProductService

Avem o interfata numita `ProductProvider` care ofera doua metode de a obtine produse. Functia `getProducts` returneaza o lista de produse, iar functia `searchForProduct` returneaza produsele dintr-o anumita categorie. 
 

```java
import java.util.List;

public interface ProductProvider {
    List<Product> getProducts();
    List<Product> searchForProduct(String query);
}
```

Clasa `ProductService` are o metoda numita `computeAveragePricesOfPhones` care calculeaza pretul mediu al telefoanelor. Pentru a obtine preturile telefoanelor, se foloseste metoda `searchForProduct` din interfata `ProductProvider`. Implementarea concreta a interfetei `ProductProvider` este clasa `ProductProviderImpl` care foloseste un API extern pentru a obtine produsele. 

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

> Inainte de a fi folosita functia `computeAveragePricesOfPhones`, trebuie sa setam interfata `ProductProvider` cu o implementare concreta folosind metoda `setProductProvider`. 


### Testarea clasei ProductService

Clasa `ProductService` depinde de o interfata `ProductProvider`. Pentru a testa clasa `ProductService`, trebuie sa folosim o implementare concreta a interfetei `ProductProvider`. Problema e ca nu putem folosi implementarea concreta a interfetei `ProductProvider` pentru ca aceasta foloseste un API extern. Acest lucru face testarea sa nu fie repetabila. (Rularea testelor va necesita acces la internet sau pot aparea probleme de conexiune sau API-ul extern poate fi indisponibil sau chiar sa isi schimbe datele returnate). 

Din acest motiv, inlocuim implementarea concreta a interfetei `ProductProvider` cu un mock. In continuare se vor prezenta trei tipuri de mock-uri, dar metodele prezentate nu sunt singurele variante de a folosi mock-urile.

#### 1. Clasa inline / anonima

Testul urmatorul foloseste o clasa anonima pentru a crea un mock. Trebuie implementate toate metodele din interfata `ProductProvider` cu niste valori concrete pentru cazul de test. In cazul nostru, testul verifica sitatuia cand nu exista niciun produs in lista de produse, de aceea se returneaza o lista goala. 

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

#### 2. Clasa separata

Similar cu clasa anonima, testul urmator foloseste o clasa separata pentru a crea un mock. In cazul nostru, testul verifica sitatuia cand exista un singur produs in lista de produse.

Mock-ul trebuie sa implementeze toate metodele interfetei `ProductProvider`. Cum nu folosim metoda `getProducts` in cazul de test prezentat, putem sa o implementam cum vrem.

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

In continuare vom prezenta un test care foloseste Mockito. In cazul nostru, testul verifica sitatuia cand exista mai multe produse in lista de produse.

Dezavantajul metodelor anterioare in care se creeaza clase anonime sau separate este necesitatea implementarii metodelor care nu sunt folosite in test. In timp se creeaza diverse clase pentru diferitele cazuri de test, iar codul poate devine greu de urmarit si intretinut.

Biblioteca Mockito ofera o metoda mai simpla de a crea mock-uri in care nu este necesara implementarea metodelor care nu sunt folosite in test.

#### Dependinte

Pentru a folosi Mockito, trebuie sa adaugam dependinta in `pom.xml`:

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>4.6.1</version>
    <scope>test</scope>
</dependency>
```

Mockito se integreaza cu Junit5 folosind dependinta: 

```xml 
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>4.6.1</version>
    <scope>test</scope>
</dependency>
```

#### Declararea mock-ului

Pentru ca Mockito sa poata crea mock-uri in JUnit5, trebuie adaugata o anotare deasupra clasei de test. 

```java
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
}
```

Pentru a crea un mock folosind Mockito, trebuie adaugata anotarea `@Mock` deasupra variabilei ce se vrea a fi mock-uita. Anotarea functioneaza atat pentru parametri metodelor de test, cat si pentru campurile clasei de test. Oricare dintre aceste variante se poate folosi, dar este recomandat sa se foloseasca consistent in toate cazurile de test.

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

Pana in acest moment, am initializat proiectul si clasa de test si am prezentat 2 moduri de a crea mockuri. In continuare vom prezenta cum se folosesc mock-urile pentru a testa metoda `computeAveragePricesOfPhones`.


Pentru a inlocui implementarea metodei `searchForProduct` din `ProductProvider`, vom folosi constructia `when .. thenReturn`. Aceasta constructie se interpreteaza astfel: cand productService (clasa testata) va apela functia `searchForProduct` cu parametrul `"phone"`, atunci se va returna o lista cu un singur produs. 

```java
when(productProvider.searchForProduct("phone"))
    .thenReturn(new ArrayList<>(Collections.singletonList(
            Product.builder().id(1).price(200).build())));
```

Aceasta constructie inlocuieste crearea de clase anonime sau separate prezentate mai sus, iar testul este mai lizibil.

Testul care verifica situatia cand exista mai multe produse in lista de produse arata astfel:

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

Constructia `when .. thenReturn` poate fi folosita de mai multe ori intr-un caz de test pentru a surprinde diferite situatii. Mockito salveaza valorile de retur pentru fiecare apel mock-uit in functie de numele functiilor si parametrii acestora.

De exemplu, un apel mock-uit al functiei `searchForProduct` cu parametrul `"phone"` va returna o lista cu un singur produs, iar un apel mock-uit al functiei `searchForProduct` cu parametrul `"laptop"` va returna o lista cu 2 produse. In functie de implementarea concrete a clasei care apeleaza `searchForProduct` se vor returna diferite rezultate.

```java
when(productProviderMock.searchForProduct("phone")).thenReturn(new ArrayList<>(Collections.singletonList(
        Product.builder().id(1).category("phone").price(200).build()
)));
when(productProviderMock.searchForProduct("laptop")).thenReturn(new ArrayList<>(Arrays.asList(
    Product.builder().id(1).price(100).category("laptop").build(),
    Product.builder().id(1).price(300).category("laptop").build()
)));
```

> Observatie: Pentru functii care nu returneaza nimic (void), nu se foloseste `when .. thenReturn`

Ca o functie mock-uita sa arunce exceptii, se folosesc urmatoarele constructii:

Pentru functii care returneaza ceva, nu sunt void
```java
when(productProviderMock.searchForProduct("phone")).thenThrow(new RuntimeException("Error"));
when(productProviderMock.searchForProduct("phone")).thenThrow(NullPointerException.class);
```
Pentru functii void
```java
doThrow(new RuntimeException("Error")).when(loggerMock).log(anyString());
doThrow(IllegalStateException.class).when(loggerMock).log(anyString());
```

--- 

#### Argument Matchers

In unele cazuri nu ne interesaza valoarea parametrilor metodelor mock-uite, ci doar faptul ca acestea au fost apelate. In acest caz, Mockito ofera posibilitatea de a folosi argument matchers. Argument matchers sunt folosite pentru a verifica daca un anumit parametru a fost apelat cu o valoare anume sau nu. Argument matchers sunt folosite in combinatie cu `when .. thenReturn` si `verify`.

```java
when(productProvider.searchForProduct(anyString())).thenReturn(products);
```

Cateva exemple sunt:
- `anyString()`
- `anyInt()`
- `anyDouble()`
- `anyBoolean()`
- `any()`
- `any(Example.class)` - potriveste valoarea parametrului cu orice valoare de tipul `Example`
- `anyList()`
- `eq("valoare")` - potriveste valoarea parametrului cu valoarea `"valoare"`

Lista completa se gaseste [aici](https://javadoc.io/static/org.mockito/mockito-core/3.3.3/org/mockito/ArgumentMatchers.html)

> Observatie: Daca se foloseste cel putin un argument matcher, atunci toate parametrii trebuie sa fie potriviti cu argument matchers.

```java
when(service.search("value", 2).thenReturn(products); // Ok
when(service.search(anyString(), anyInt()).thenReturn(products); // Ok
when(service.search(eq("value"), anyInt()).thenReturn(products); // Ok
when(service.search(anyString(), eq(2)).thenReturn(products); // Ok
when(service.search(eq("value"), eq(2)).thenReturn(products); // Ok
when(service.search("value", anyInt()).thenReturn(products); // Nu e ok, va da eroare
```

---

#### Verificarea apelurilor metodelor mock-uite


In cazul in care se doreste sa se verifice daca o metoda a fost apelata, se foloseste `verify`. `verify` poate fi folosit in combinatie cu `when .. thenReturn` sau fara aceasta.

```java
when(productProvider.searchForProduct("phone")).thenReturn(products);

productService.computeAveragePricesOfPhones();

verify(productProvider).searchForProduct("phone");
```

In cazul in care se doreste sa se verifice daca o metoda a fost apelata de un anumit numar de ori, se foloseste `verify` in combinatie cu `times`.

```java
when(productProvider.searchForProduct("phone")).thenReturn(products);

productService.computeAveragePricesOfPhones();
productService.computeAveragePricesOfPhones();

verify(productProvider, times(2)).searchForProduct("phone");
```

In cazul in care se doreste sa se verifice daca o metoda a fost apelata cu un anumit parametru, dar fara se ne intereseze valoarea, se foloseste `verify` in combinatie cu `ArgumentMatchers`.

```java
when(productProvider.searchForProduct("phone")).thenReturn(products);

productService.computeAveragePricesOfPhones();

verify(productProvider).searchForProduct(anyString());
```

Cateva exemple pentru tipul de verificare a numarului de apeuri sunt:
- `times(2)` - verifica daca metoda a fost apelata de 2 ori
- `atLeast(2)` - verifica daca metoda a fost apelata de cel putin 2 ori
- `atLeastOnce()` - verifica daca metoda a fost apelata cel putin o data
- `atMost(2)` - verifica daca metoda a fost apelata de cel mult 2 ori
- `never()` - verifica daca metoda nu a fost apelata
- `only()` - verifica daca metoda a fost apelata o singura data

Mai multe informatii se pot gasi la urmatorul [link](https://site.mockito.org/javadoc/current/org/mockito/verification/class-use/VerificationMode.html)


Implementarea cu toate elementele prezentate mai sus se gaseste in exemplul de mai jos:

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

## Exercitii

Pornind de la codul gasit [aici](https://github.com/MarioRivis/vvs-mockito), implementati si testati urmatorele metode ale clasei `ProductService`:

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