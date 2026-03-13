package com.example.demo.repository;

import com.example.demo.entity.Bids;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface BidsRepository extends JpaRepository<Bids, Long> {

    @Query("select max(b.bidAmount) from Bids b where b.auctionFk = :auctionId")
    BigDecimal findMaxBidAmountByAuctionFk(@Param("auctionId") Long auctionId);

    Optional<Bids> findTopByAuctionFkOrderByBidDateDesc(Long auctionFk);
}

