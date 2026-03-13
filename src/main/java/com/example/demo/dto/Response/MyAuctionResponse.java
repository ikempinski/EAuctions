package com.example.demo.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyAuctionResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal startPrice;
    private BigDecimal currentBid;
    private LocalDateTime endDate;
    private String photoName;
    private String highestBidderUsername;
    private Boolean finished;
    private Long isActive;
    private String winnerUsername;
    private String winnerEmail;
    private BigDecimal finalPrice;
}

