package com.tevin.inside;

public class Items {
    public String ItemImage, ItemName, ItemQuantity;

    public Items() {
    }

    public Items(String item_image, String item_name, String item_quantity) {
        this.ItemImage = item_image;
        this.ItemName = item_name;
        this.ItemQuantity = item_quantity;
    }

    public String getItem_image() {
        return ItemImage;
    }

    public void setItem_image(String item_image) {
        this.ItemImage = item_image;
    }

    public String getItem_name() {
        return ItemName;
    }

    public void setItem_name(String item_name) {
        this.ItemName = item_name;
    }

    public String getItem_quantity() {
        return ItemQuantity;
    }

    public void setItem_quantity(String item_quantity) {
        this.ItemQuantity = item_quantity;
    }
}
