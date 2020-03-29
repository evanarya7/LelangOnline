package com.defalt.lelangonline.ui.home;

import java.sql.Timestamp;

public class TopAuction {

    private String auctionID;
    private String itemName;
    private Double itemValue;
    private Double priceStart;
    private String itemImg;
    private int favCount;
    private Timestamp auctionStart;
    private Timestamp auctionEnd;
    private Timestamp serverTime;

    public TopAuction(String auctionID, Timestamp auctionStart, Timestamp auctionEnd,
               String itemName, Double itemValue, Double priceStart, String itemImg, int favCount,
               Timestamp serverTime) {
        this.auctionID = auctionID;
        this.auctionStart = auctionStart;
        this.auctionEnd = auctionEnd;
        this.itemName = itemName;
        this.itemValue = itemValue;
        this.priceStart = priceStart;
        this.itemImg = itemImg;
        this.favCount = favCount;
        this.serverTime = serverTime;
    }

    TopAuction() { }

    String getAuctionID() {
        return auctionID;
    }

    void setAuctionID(String auctionID) {
        this.auctionID = auctionID;
    }

    String getItemName() {
        return itemName;
    }

    void setItemName(String itemName) {
        this.itemName = itemName;
    }

    Double getItemValue() {
        return itemValue;
    }

    void setItemValue(Double itemValue) {
        this.itemValue = itemValue;
    }

    Double getPriceStart() {
        return priceStart;
    }

    void setPriceStart(Double priceStart) {
        this.priceStart = priceStart;
    }

    String getItemImg() {
        return itemImg;
    }

    void setItemImg(String itemImg) {
        this.itemImg = itemImg;
    }

    int getFavCount() {
        return favCount;
    }

    void setFavCount(int favCount) {
        this.favCount = favCount;
    }

    Timestamp getAuctionStart() {
        return auctionStart;
    }

    void setAuctionStart(Timestamp auctionStart) {
        this.auctionStart = auctionStart;
    }

    Timestamp getAuctionEnd() {
        return auctionEnd;
    }

    void setAuctionEnd(Timestamp auctionEnd) {
        this.auctionEnd = auctionEnd;
    }

    Timestamp getServerTime() {
        return serverTime;
    }

    void setServerTime(Timestamp serverTime) {
        this.serverTime = serverTime;
    }
}
