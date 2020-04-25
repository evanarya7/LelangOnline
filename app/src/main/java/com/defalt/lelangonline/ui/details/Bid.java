package com.defalt.lelangonline.ui.details;

import java.sql.Timestamp;

public class Bid {
    private String userName;
    private String userImage;
    private Double bidPrice;
    private Timestamp bidTime;

    Bid() { }

    public Bid(String userName, String userImage, Double bidPrice, Timestamp bidTime) {
        this.userName = userName;
        this.userImage = userImage;
        this.bidPrice = bidPrice;
        this.bidTime = bidTime;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public Double getBidPrice() {
        return bidPrice;
    }

    public Timestamp getBidTime() {
        return bidTime;
    }
}
