package com.example.demo.controller;

import com.example.demo.entity.Auction;
import com.example.demo.services.AuctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/auction")
@RequiredArgsConstructor
@Slf4j
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping("/create")
    public ResponseEntity<Auction> createAuction(@RequestParam("title") String title,
                                                 @RequestParam("description") String description,
                                                 @RequestParam("startPrice") String startPrice,
                                                 @RequestParam("endDate") String endDate,
                                                 @RequestParam("photo") MultipartFile photo) {
        try {
            Auction created = auctionService.createAuction(title, description, startPrice, endDate, photo);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid auction create request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Failed to create auction", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyAuctions() {
        try {
            return ResponseEntity.ok(auctionService.getMyAuctions());
        } catch (IllegalStateException e) {
            log.warn("Unauthorized access to my auctions: {}", e.getMessage());
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            log.error("Failed to load my auctions", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/guest")
    public ResponseEntity<?> getGuestAuctions() {
        try {
            return ResponseEntity.ok(auctionService.getActiveAuctionsForGuest());
        } catch (Exception e) {
            log.error("Failed to load guest auctions", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/photo/{fileName}")
    public ResponseEntity<Resource> getPhoto(@PathVariable String fileName) {
        try {
            Resource resource = auctionService.loadPhotoAsResource(fileName);
            String contentType = "image/jpeg";
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (IOException e) {
            log.warn("Photo not found: {}", fileName, e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAuction(@PathVariable Long id) {
        try {
            auctionService.deleteAuction(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            log.warn("Unauthorized delete attempt for auction {}: {}", id, e.getMessage());
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            log.error("Failed to delete auction {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/bid/{id}")
    public ResponseEntity<?> placeBid(@PathVariable Long id, @RequestParam("amount") String amount) {
        try {
            BigDecimal placed = auctionService.placeBid(id, amount);
            return ResponseEntity.ok(placed);
        } catch (IllegalStateException e) {
            log.warn("Failed to place bid on auction {}: {}", id, e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid bid for auction {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to place bid on auction {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

