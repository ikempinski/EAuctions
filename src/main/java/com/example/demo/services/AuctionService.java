package com.example.demo.services;

import com.example.demo.dto.Response.GuestAuctionResponse;
import com.example.demo.dto.Response.MyAuctionResponse;
import com.example.demo.entity.Auction;
import com.example.demo.entity.AuctionWinners;
import com.example.demo.entity.Bids;
import com.example.demo.entity.User;
import com.example.demo.repository.AuctionRepository;
import com.example.demo.repository.AuctionWinnersRepository;
import com.example.demo.repository.BidsRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.AuthUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final BidsRepository bidsRepository;
    private final AuctionWinnersRepository auctionWinnersRepository;

    private final Path uploadDir = Paths.get("uploads", "auctions");

    public Auction createAuction(String title,
                                 String description,
                                 String startPrice,
                                 String endDate,
                                 MultipartFile photo) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("User must be authenticated to create an auction");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof AuthUserPrincipal)) {
            throw new IllegalStateException("Unexpected authentication principal type");
        }
        AuthUserPrincipal authUser = (AuthUserPrincipal) principal;

        if (photo == null || photo.isEmpty()) {
            throw new IllegalArgumentException("Photo file is required");
        }

        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String extension = "";
        String originalName = photo.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }
        String storedFileName = UUID.randomUUID() + extension;
        Path targetPath = uploadDir.resolve(storedFileName);
        Files.copy(photo.getInputStream(), targetPath);

        Auction auction = new Auction();
        auction.setTitle(title);
        auction.setDescription(description);
        auction.setIsActive(1L);
        auction.setStartPrice(new BigDecimal(startPrice));
        auction.setEndDate(LocalDateTime.parse(endDate));
        auction.setPhotoName(storedFileName);
        auction.setUserFk(authUser.getId());

        Auction saved = auctionRepository.save(auction);
        log.info("Created auction id={} with photo {}", saved.getId(), storedFileName);
        return saved;
    }

    public List<MyAuctionResponse> getMyAuctions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("User must be authenticated to view their auctions");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof AuthUserPrincipal)) {
            throw new IllegalStateException("Unexpected authentication principal type");
        }
        AuthUserPrincipal authUser = (AuthUserPrincipal) principal;
        List<Auction> auctions = auctionRepository.findByUserFkAndIsActive(authUser.getId(), 1L);
        return auctions.stream().map(a -> {
            BigDecimal currentBid = getCurrentBidForAuction(a);
            String highestBidder = getHighestBidderUsername(a);
            return new MyAuctionResponse(
                    a.getId(),
                    a.getTitle(),
                    a.getDescription(),
                    a.getStartPrice(),
                    currentBid,
                    a.getEndDate(),
                    a.getPhotoName(),
                    highestBidder
            );
        }).toList();
    }

    public Resource loadPhotoAsResource(String fileName) throws MalformedURLException {
        Path filePath = uploadDir.resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new MalformedURLException("File not found: " + fileName);
        }
        return resource;
    }

    public void deleteAuction(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("User must be authenticated to delete an auction");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof AuthUserPrincipal)) {
            throw new IllegalStateException("Unexpected authentication principal type");
        }
        AuthUserPrincipal authUser = (AuthUserPrincipal) principal;

        Auction auction = auctionRepository.findByIdAndUserFk(id, authUser.getId())
                .orElseThrow(() -> new IllegalStateException("Auction not found or does not belong to current user"));

        auction.setIsActive(0L);
        auctionRepository.save(auction);

        if (auction.getPhotoName() != null && !auction.getPhotoName().isBlank()) {
            try {
                Path filePath = uploadDir.resolve(auction.getPhotoName()).normalize();
                Files.deleteIfExists(filePath);
                log.info("Deleted photo file for auction {}: {}", id, filePath);
            } catch (IOException e) {
                log.warn("Failed to delete photo file for auction {}: {}", id, auction.getPhotoName(), e);
            }
        }
    }

    public List<GuestAuctionResponse> getActiveAuctionsForGuest() {
        // active auctions whose end date is in the future
        final Long currentUserId = getCurrentUserIdOrNull();
        List<Auction> auctions = auctionRepository.findByIsActiveAndEndDateAfter(1L, LocalDateTime.now());
        return auctions.stream().map(a -> {
            User owner = userRepository.findById(a.getUserFk())
                    .orElse(null);
            String username = owner != null ? owner.getUsername() : "Nieznany";
            BigDecimal currentBid = getCurrentBidForAuction(a);
            boolean myCurrentBid = hasCurrentUserHighestBid(a, currentUserId);
            return new GuestAuctionResponse(
                    a.getId(),
                    a.getTitle(),
                    a.getDescription(),
                    a.getStartPrice(),
                    currentBid,
                    a.getEndDate(),
                    a.getPhotoName(),
                    username,
                    myCurrentBid
            );
        }).toList();
    }

    private Long getCurrentUserIdOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthUserPrincipal authUserPrincipal) {
            return authUserPrincipal.getId();
        }
        return null;
    }

    private BigDecimal getCurrentBidForAuction(Auction auction) {
        BigDecimal maxBid = bidsRepository.findMaxBidAmountByAuctionFk(auction.getId());
        return maxBid != null ? maxBid : auction.getStartPrice();
    }

    private String getHighestBidderUsername(Auction auction) {
        return bidsRepository.findTopByAuctionFkOrderByBidDateDesc(auction.getId())
                .flatMap(b -> userRepository.findById(b.getUserFk()))
                .map(User::getUsername)
                .orElse(null);
    }

    private boolean hasCurrentUserHighestBid(Auction auction, Long currentUserId) {
        if (currentUserId == null) {
            return false;
        }
        return bidsRepository.findTopByAuctionFkOrderByBidDateDesc(auction.getId())
                .map(b -> b.getUserFk().equals(currentUserId))
                .orElse(false);
    }

    public BigDecimal placeBid(Long auctionId, String amount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("User must be authenticated to place a bid");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof AuthUserPrincipal)) {
            throw new IllegalStateException("Unexpected authentication principal type");
        }
        AuthUserPrincipal authUser = (AuthUserPrincipal) principal;

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalStateException("Auction not found"));

        if (!auction.getIsActive().equals(1L) || auction.getEndDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Auction is not active");
        }

        if (auction.getUserFk().equals(authUser.getId())) {
            throw new IllegalStateException("Owner cannot bid on own auction");
        }

        BigDecimal bidAmount = new BigDecimal(amount);
        BigDecimal currentBid = getCurrentBidForAuction(auction);
        if (bidAmount.compareTo(currentBid) <= 0) {
            throw new IllegalArgumentException("Bid amount must be greater than current bid");
        }

        Bids bid = new Bids();
        bid.setAuctionFk(auction.getId());
        bid.setUserFk(authUser.getId());
        bid.setBidAmount(bidAmount);
        bid.setBidDate(LocalDateTime.now());
        bidsRepository.save(bid);

        return bidAmount;
    }

    /**
     * Periodically close finished auctions and record winners.
     * Runs every 60 seconds.
     */
    @Scheduled(fixedDelay = 60_000)
    public void closeFinishedAuctions() {
        List<Auction> finished = auctionRepository.findByIsActiveAndEndDateBefore(1L, LocalDateTime.now());
        for (Auction auction : finished) {
            // skip if already have a winner recorded
            if (auctionWinnersRepository.existsByAuctionFk(auction.getId())) {
                continue;
            }

            // find highest/last bid
            Bids topBid = bidsRepository.findTopByAuctionFkOrderByBidDateDesc(auction.getId())
                    .orElse(null);

            if (topBid != null) {
                AuctionWinners winner = new AuctionWinners();
                winner.setAuctionFk(auction.getId());
                winner.setUserFk(topBid.getUserFk());
                winner.setBidAmount(topBid.getBidAmount());
                auctionWinnersRepository.save(winner);
            }

            auction.setIsActive(0L);
            auctionRepository.save(auction);
        }
    }
}

