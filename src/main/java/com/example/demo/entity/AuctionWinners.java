package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "auction_winners")
public class AuctionWinners {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long auctionFk;

    @Column(nullable = false)
    private Long userFk;

    @Column(nullable = false)
    private BigDecimal bidAmount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAuctionFk() { return auctionFk; }
    public void setAuctionFk(Long auctionFk) { this.auctionFk = auctionFk; }
    public Long getUserFk() { return userFk; }
    public void setUserFk(Long userFk) { this.userFk = userFk; }
    public BigDecimal getBidAmount() { return bidAmount; }
    public void setBidAmount(BigDecimal bidAmount) { this.bidAmount = bidAmount; }
}
