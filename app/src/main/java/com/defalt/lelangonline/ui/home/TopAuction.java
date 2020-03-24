package com.defalt.lelangonline.ui.home;

import java.sql.Timestamp;

public class TopAuction {

    private String auctionID;
    private String itemName;
    private Double itemValue;
    private Double priceStart;
    private int favCount;
    private Timestamp auctionStart;
    private Timestamp auctionEnd;
    private Timestamp serverTime;

    public TopAuction(String auctionID, Timestamp auctionStart, Timestamp auctionEnd,
               String itemName, Double itemValue, Double priceStart, int favCount,
               Timestamp serverTime) {
        this.auctionID = auctionID;
        this.auctionStart = auctionStart;
        this.auctionEnd = auctionEnd;
        this.itemName = itemName;
        this.itemValue = itemValue;
        this.priceStart = priceStart;
        this.favCount = favCount;
        this.serverTime = serverTime;
    }

    TopAuction() { }

    String getAuctionID() {
        return auctionID;
    }

    public void setAuctionID(String auctionID) {
        this.auctionID = auctionID;
    }

    String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    Double getItemValue() {
        return itemValue;
    }

    public void setItemValue(Double itemValue) {
        this.itemValue = itemValue;
    }

    Double getPriceStart() {
        return priceStart;
    }

    public void setPriceStart(Double priceStart) {
        this.priceStart = priceStart;
    }

    public int getFavCount() {
        return favCount;
    }

    public void setFavCount(int favCount) {
        this.favCount = favCount;
    }

    Timestamp getAuctionStart() {
        return auctionStart;
    }

    public void setAuctionStart(Timestamp auctionStart) {
        this.auctionStart = auctionStart;
    }

    Timestamp getAuctionEnd() {
        return auctionEnd;
    }

    public void setAuctionEnd(Timestamp auctionEnd) {
        this.auctionEnd = auctionEnd;
    }

    Timestamp getServerTime() {
        return serverTime;
    }

    public void setServerTime(Timestamp serverTime) {
        this.serverTime = serverTime;
    }
}
