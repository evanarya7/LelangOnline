package com.defalt.lelangonline.ui.details;

import java.sql.Timestamp;

public class Details {
    private String itemName;
    private String itemDesc;
    private String itemCategory;
    private String itemImg;
    private Double itemInitPrice;
    private Double itemStartPrice;
    private Double itemLimitPrice;
    private int itemLikeCount;
    private int bidCount;
    private Timestamp timeStart;
    private Timestamp timeEnd;
    private Timestamp timeServer;

    public Details() {
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getItemImg() {
        return itemImg;
    }

    public void setItemImg(String itemImg) {
        this.itemImg = itemImg;
    }

    public Double getItemInitPrice() {
        return itemInitPrice;
    }

    public void setItemInitPrice(Double itemInitPrice) {
        this.itemInitPrice = itemInitPrice;
    }

    public Double getItemStartPrice() {
        return itemStartPrice;
    }

    public void setItemStartPrice(Double itemStartPrice) {
        this.itemStartPrice = itemStartPrice;
    }

    public Double getItemLimitPrice() {
        return itemLimitPrice;
    }

    public void setItemLimitPrice(Double itemLimitPrice) {
        this.itemLimitPrice = itemLimitPrice;
    }

    public int getItemLikeCount() {
        return itemLikeCount;
    }

    public void setItemLikeCount(int itemLikeCount) {
        this.itemLikeCount = itemLikeCount;
    }

    public int getBidCount() {
        return bidCount;
    }

    public void setBidCount(int bidCount) {
        this.bidCount = bidCount;
    }

    public Timestamp getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Timestamp timeStart) {
        this.timeStart = timeStart;
    }

    public Timestamp getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Timestamp timeEnd) {
        this.timeEnd = timeEnd;
    }

    public Timestamp getTimeServer() {
        return timeServer;
    }

    public void setTimeServer(Timestamp timeServer) {
        this.timeServer = timeServer;
    }
}
