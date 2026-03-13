package com.example.demo.repository;

import com.example.demo.entity.AuctionWinners;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionWinnersRepository extends JpaRepository<AuctionWinners, Long> {

    boolean existsByAuctionFk(Long auctionFk);
}

