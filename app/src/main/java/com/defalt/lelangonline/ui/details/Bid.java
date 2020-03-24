package com.defalt.lelangonline.ui.details;

import java.sql.Timestamp;

public class Bid {
    private String userName;
    private Double bidPrice;
    private Timestamp bidTime;

    Bid() { }

    public Bid(String userName, Double bidPrice, Timestamp bidTime) {
        this.userName = userName;
        this.bidPrice = bidPrice;
        this.bidTime = bidTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(Double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public Timestamp getBidTime() {
        return bidTime;
    }

    public void setBidTime(Timestamp bidTime) {
        this.bidTime = bidTime;
    }
}
