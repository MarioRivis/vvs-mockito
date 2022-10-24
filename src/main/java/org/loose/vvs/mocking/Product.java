package org.loose.vvs.mocking;

import java.util.List;

public class Product {
    private long id;
    private String title;
    private String description;
    private int price;
    private double discountPercentage;
    private double rating;
    private int stock;
    private String brand;
    private String category;
    private String thumbnail;
    private List<String> images;

    public Product() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        return id == product.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", discountPercentage=" + discountPercentage +
                ", rating=" + rating +
                ", stock=" + stock +
                ", brand='" + brand + '\'' +
                ", category='" + category + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", images=" + images +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public static ProductBuilder builder() {
        return ProductBuilder.aProduct();
    }

    public static final class ProductBuilder {
        private long id;
        private String title;
        private String description;
        private int price;
        private double discountPercentage;
        private double rating;
        private int stock;
        private String brand;
        private String category;
        private String thumbnail;
        private List<String> images;

        private ProductBuilder() {
        }

        private static ProductBuilder aProduct() {
            return new ProductBuilder();
        }

        public ProductBuilder id(long id) {
            this.id = id;
            return this;
        }

        public ProductBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ProductBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ProductBuilder price(int price) {
            this.price = price;
            return this;
        }

        public ProductBuilder discountPercentage(double discountPercentage) {
            this.discountPercentage = discountPercentage;
            return this;
        }

        public ProductBuilder rating(double rating) {
            this.rating = rating;
            return this;
        }

        public ProductBuilder stock(int stock) {
            this.stock = stock;
            return this;
        }

        public ProductBuilder brand(String brand) {
            this.brand = brand;
            return this;
        }

        public ProductBuilder category(String category) {
            this.category = category;
            return this;
        }

        public ProductBuilder thumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        public ProductBuilder images(List<String> images) {
            this.images = images;
            return this;
        }


        public Product build() {
            Product product = new Product();
            product.setId(id);
            product.setTitle(title);
            product.setDescription(description);
            product.setPrice(price);
            product.setDiscountPercentage(discountPercentage);
            product.setRating(rating);
            product.setStock(stock);
            product.setBrand(brand);
            product.setCategory(category);
            product.setThumbnail(thumbnail);
            product.setImages(images);
            return product;
        }
    }
}
