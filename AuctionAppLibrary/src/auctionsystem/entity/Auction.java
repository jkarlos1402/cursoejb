/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auctionsystem.entity;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Curso
 */
public class Auction implements Serializable{

    private Integer id;

    private double startAmount;

    private String status;

    private Date openTime;

    private Date closeTime;

    private Item item;

    private User seller;
    
    private static int countId = 0;

    public Auction() {
        this.countId++;
        this.id = countId;
    }

    public Auction(double startAmount, String status, Date openTime, Date closeTime, Item item, User seller) {
        this();
        this.startAmount = startAmount;
        this.status = status;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.item = item;
        this.seller = seller;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    public Date getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Date openTime) {
        this.openTime = openTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getStartAmount() {
        return startAmount;
    }

    public void setStartAmount(double startAmount) {
        this.startAmount = startAmount;
    }

    public Integer getId() {
        return id;
    }

}
