package com.example.demo.repository;

import com.example.demo.entity.AuctionWinners;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionWinnersRepository extends JpaRepository<AuctionWinners, Long> {

    boolean existsByAuctionFk(Long auctionFk);

    List<AuctionWinners> findByUserFk(Long userFk);

    Optional<AuctionWinners> findByAuctionFk(Long auctionFk);
}

