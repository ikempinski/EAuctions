package com.example.demo.repository;

import com.example.demo.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    List<Auction> findByUserFk(Long userFk);
    List<Auction> findByUserFkAndIsActive(Long userFk, Long isActive);
    Optional<Auction> findByIdAndUserFk(Long id, Long userFk);
    List<Auction> findByIsActiveAndEndDateAfter(Long isActive, LocalDateTime endDate);
    List<Auction> findByIsActiveAndEndDateBefore(Long isActive, LocalDateTime endDate);
    Optional<Auction> findTopByUserFkOrderByCreatedAtDesc(Long userFk);
}

