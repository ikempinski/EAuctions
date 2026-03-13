import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuctionService } from '../_services/auction.service';
import { UserService } from '../_services/user.service';
import type { Auction } from '../_models/auction.model';

@Component({
  selector: 'app-guest-view',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './guest-view.component.html'
})
export class GuestViewComponent implements OnInit {
  auctions: Auction[] = [];
  filteredAuctions: Auction[] = [];
  searchTerm = '';
  sortBy: 'endDate' | 'startPrice' | 'currentBid' = 'endDate';
  sortDirection: 'asc' | 'desc' = 'asc';
  currentUsername: string | null = null;
  previewPhotoName: string | null = null;
  bidInputs: Record<number, number | null> = {};
  bidErrors: Record<number, string | null> = {};
  placingBidForId: number | null = null;

  constructor(
    private auctionService: AuctionService,
    private userService: UserService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // Try to load current user; ignore errors (guest)
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUsername = user.username;
      },
      error: () => {
        this.currentUsername = null;
      },
    });
    this.auctionService.getGuestAuctions().subscribe({
      next: (list) => {
        this.auctions = list;
        this.cdr.detectChanges();
        this.applyFilters();
      },
    });
  }

  onSearchChange(term: string): void {
    this.searchTerm = term;
    this.applyFilters();
  }

  onSortChange(value: string): void {
    this.sortBy = value as any;
    this.applyFilters();
  }

  onDirectionChange(value: string): void {
    this.sortDirection = value as 'asc' | 'desc';
    this.applyFilters();
  }

  private applyFilters(): void {
    const term = this.searchTerm.toLowerCase().trim();
    let result = this.auctions;

    if (term) {
      result = result.filter((a) => a.title.toLowerCase().includes(term));
    }

    result = [...result].sort((a, b) => {
      let cmp = 0;
      switch (this.sortBy) {
        case 'startPrice':
          cmp = a.startPrice - b.startPrice;
          break;
        case 'currentBid':
          // fallback to startPrice if currentBid is not present
          const bidA = a.currentBid ?? a.startPrice;
          const bidB = b.currentBid ?? b.startPrice;
          cmp = bidA - bidB;
          break;
        case 'endDate':
        default:
          cmp = new Date(a.endDate).getTime() - new Date(b.endDate).getTime();
          break;
      }

      return this.sortDirection === 'asc' ? cmp : -cmp;
    });

    this.filteredAuctions = result;
    this.cdr.detectChanges();
  }

  openPhoto(photoName: string): void {
    this.previewPhotoName = photoName;
  }

  closePhoto(): void {
    this.previewPhotoName = null;
  }

  canBid(a: Auction): boolean {
    return !!this.currentUsername && a.ownerUsername !== this.currentUsername;
  }

  getCurrentPrice(a: Auction): number {
    return a.currentBid ?? a.startPrice;
  }

  placeBid(a: Auction): void {
    if (!this.canBid(a)) {
      return;
    }
    const amount = this.bidInputs[a.id];
    this.bidErrors[a.id] = null;
    if (amount == null || isNaN(amount) || amount <= 0) {
      this.bidErrors[a.id] = 'Podaj poprawną kwotę.';
      return;
    }

    if (amount <= (a.currentBid ?? a.startPrice)) {
      this.bidErrors[a.id] = 'Oferta jest niższa niż aktualna cena.';
      return;
    }

    this.placingBidForId = a.id;
    this.auctionService.placeBid(a.id, amount).subscribe({
      next: (newBid) => {
        a.currentBid = newBid;
        this.bidInputs[a.id] = null;
        this.ngOnInit();
      },
      error: (err) => {
        const message = err?.error ?? 'Nie udało się złożyć oferty.';
        this.bidErrors[a.id] = typeof message === 'string' ? message : 'Nie udało się złożyć oferty.';
        this.cdr.detectChanges();
      },
      complete: () => {
        this.placingBidForId = null;
        this.cdr.detectChanges();
      },
    });
  }
}
