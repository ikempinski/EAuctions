package com.example.demo.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WonAuctionResponse {
    private Long auctionId;
    private String title;
    private BigDecimal finalPrice;
    private LocalDateTime endDate;
    private String photoName;
}

