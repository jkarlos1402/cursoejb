
package auctionsystem.dto;

import java.io.Serializable;

public class PlaceBidMessage implements Serializable{
    
    private Integer auctionId;
    private Integer bidderId;
    private Double amount;

    public PlaceBidMessage(Integer auctionId, Integer bidderId, Double amount) {
        this.auctionId = auctionId;
        this.bidderId = bidderId;
        this.amount = amount;
    }

    public Double getAmount() {
        return amount;
    }

    public Integer getBidderId() {
        return bidderId;
    }
        
    public Integer getAuctionId() {
        return auctionId;
    }
        
}
