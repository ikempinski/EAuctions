package com.example.demo.services;

import com.example.demo.dto.Response.GuestAuctionResponse;
import com.example.demo.entity.Auction;
import com.example.demo.entity.Bids;
import com.example.demo.entity.User;
import com.example.demo.repository.AuctionRepository;
import com.example.demo.repository.BidsRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTest {

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BidsRepository bidsRepository;

    @InjectMocks
    private AuctionService auctionService;

    private Auction auction;
    private User owner;

    @BeforeEach
    void setUp() {
        auction = new Auction();
        auction.setId(1L);
        auction.setTitle("Test auction");
        auction.setDescription("Test description");
        auction.setUserFk(10L);
        auction.setIsActive(1L);
        auction.setStartPrice(new BigDecimal("100.00"));
        auction.setEndDate(LocalDateTime.now().plusDays(1));
        auction.setPhotoName("photo.jpg");

        owner = new User();
        owner.setId(10L);
        owner.setUsername("seller");
        owner.setEmail("seller@example.com");
    }

    @Test
    void getActiveAuctionsForGuest_mapsCurrentBidAndOwner() {
        // given
        when(auctionRepository.findByIsActiveAndEndDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of(auction));
        when(userRepository.findById(10L)).thenReturn(Optional.of(owner));

        Bids bid = new Bids();
        bid.setAuctionFk(1L);
        bid.setUserFk(20L);
        bid.setBidAmount(new BigDecimal("150.00"));
        when(bidsRepository.findMaxBidAmountByAuctionFk(1L)).thenReturn(bid.getBidAmount());

        // when
        List<GuestAuctionResponse> result = auctionService.getActiveAuctionsForGuest();

        // then
        assertThat(result).hasSize(1);
        GuestAuctionResponse dto = result.get(0);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Test auction");
        assertThat(dto.getOwnerUsername()).isEqualTo("seller");
        assertThat(dto.getCurrentBid()).isEqualByComparingTo("150.00");
    }
}

