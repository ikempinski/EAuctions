package com.example.demo.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestAuctionResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal startPrice;
    private BigDecimal currentBid;
    private LocalDateTime endDate;
    private String photoName;
    private String ownerUsername;
    private Boolean myCurrentBid;
}

