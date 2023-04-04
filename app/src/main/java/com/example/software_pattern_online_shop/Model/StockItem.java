package com.example.software_pattern_online_shop.Model;

import java.io.Serializable;

public class StockItem extends StockItemId implements Serializable {
    String title, manufacturer, category, imagePath;
    double price;
    int quantity;

    public StockItem() {}

    public StockItem(String title, String manufacturer, String category, String imagePath, double price, int quantity) {
        this.title = title;
        this.manufacturer = manufacturer;
        this.category = category;
        this.imagePath = imagePath;
        this.price = price;
        this.quantity = quantity;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getManufacturer() {
        return manufacturer;
    }
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
