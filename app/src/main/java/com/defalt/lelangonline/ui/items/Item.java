package com.defalt.lelangonline.ui.items;

public class Item {
    private String itemID;
    private String itemName;
    private String itemCat;
    private Double itemValue;
    private String itemImg;
    private int favCount;

    public Item(String itemID, String itemName, String itemCat, Double itemValue, String itemImg, int favCount) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.itemCat = itemCat;
        this.itemValue = itemValue;
        this.itemImg = itemImg;
        this.favCount = favCount;
    }

    Item() { }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemCat() {
        return itemCat;
    }

    public void setItemCat(String itemCat) {
        this.itemCat = itemCat;
    }

    public Double getItemValue() {
        return itemValue;
    }

    public void setItemValue(Double itemValue) {
        this.itemValue = itemValue;
    }

    public String getItemImg() {
        return itemImg;
    }

    public void setItemImg(String itemImg) {
        this.itemImg = itemImg;
    }

    public int getFavCount() {
        return favCount;
    }

    public void setFavCount(int favCount) {
        this.favCount = favCount;
    }
}
