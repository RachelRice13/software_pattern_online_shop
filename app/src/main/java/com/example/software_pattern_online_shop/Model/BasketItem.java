package com.example.software_pattern_online_shop.Model;

public class BasketItem extends BasketItemId {
    String itemTitle, imagePath;
    double price;
    int quantity;

    public BasketItem() {
    }

    public BasketItem(String itemTitle, String imagePath, double price, int quantity) {
        this.itemTitle = itemTitle;
        this.imagePath = imagePath;
        this.price = price;
        this.quantity = quantity;
    }

    public String getItemTitle() {
        return itemTitle;
    }
    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
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
