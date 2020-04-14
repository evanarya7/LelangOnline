package com.defalt.lelangonline.ui.auctions;

import java.sql.Timestamp;

public class Auction {

    private String auctionID;
    private String itemName;
    private Double itemValue;
    private Double priceStart;
    private String itemImg;
    private int favCount;
    private Timestamp auctionStart;
    private Timestamp auctionEnd;
    private Timestamp serverTime;

    public Auction(String auctionID, Timestamp auctionStart, Timestamp auctionEnd,
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

    Auction() { }

    public String getAuctionID() {
        return auctionID;
    }

    public void setAuctionID(String auctionID) {
        this.auctionID = auctionID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Double getItemValue() {
        return itemValue;
    }

    public void setItemValue(Double itemValue) {
        this.itemValue = itemValue;
    }

    public Double getPriceStart() {
        return priceStart;
    }

    public void setPriceStart(Double priceStart) {
        this.priceStart = priceStart;
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

    public Timestamp getAuctionStart() {
        return auctionStart;
    }

    public void setAuctionStart(Timestamp auctionStart) {
        this.auctionStart = auctionStart;
    }

    public Timestamp getAuctionEnd() {
        return auctionEnd;
    }

    public void setAuctionEnd(Timestamp auctionEnd) {
        this.auctionEnd = auctionEnd;
    }

    public Timestamp getServerTime() {
        return serverTime;
    }

    public void setServerTime(Timestamp serverTime) {
        this.serverTime = serverTime;
    }
}
