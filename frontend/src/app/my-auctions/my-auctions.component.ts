import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuctionService } from '../_services/auction.service';
import { AddAuctionModalComponent, AddAuctionPayload } from '../add-auction-modal/add-auction-modal.component';
import { Auction } from '../_models/auction.model';

@Component({
  selector: 'app-my-auctions',
  standalone: true,
  imports: [CommonModule, FormsModule, AddAuctionModalComponent],
  templateUrl: './my-auctions.component.html'
})
export class MyAuctionsComponent implements OnInit {
  okMessage = '';
  errorMessage = '';
  showAuctionModal = false;
  myAuctions: Auction[] = [];
  selectedAuction: Auction | null = null;
  showDetailsModal = false;

  myAuctionsLoading = false;
  statusFilter: 'active' | 'finished' = 'active';

  constructor(
    private auctionService: AuctionService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.myAuctionsLoading = true;
    this.errorMessage = '';
    this.auctionService.getMyAuctions().subscribe({
      next: (auctions) => {
        this.myAuctions = auctions;
        this.myAuctionsLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Nie udało się załadować Twoich aukcji.';
        this.myAuctionsLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  openAuctionModal(): void {
    this.showAuctionModal = true;
  }

  closeAuctionModal(): void {
    this.showAuctionModal = false;
  }

  createAuction(payload: AddAuctionPayload): void {
    this.errorMessage = '';
    this.okMessage = '';
    this.auctionService.createAuction(payload).subscribe({
      next: (created) => {
        this.showAuctionModal = false;
        this.ngOnInit();
      },
      error: (err) => {
        const msg =
          typeof err?.error === 'string' && err.error.length
            ? err.error
            : 'Nie udało się dodać aukcji. Spróbuj ponownie.';
        this.errorMessage = msg;
        this.closeAuctionModal();
        this.cdr.detectChanges();
      },
    });
  }

  get filteredAuctions(): Auction[] {
    if (this.statusFilter === 'active') {
      return this.myAuctions.filter((a) => !a.finished && a.isActive === 1);
    }
    return this.myAuctions.filter((a) => a.finished);
  }

  setStatusFilter(filter: 'active' | 'finished'): void {
    this.statusFilter = filter;
  }

  closeDetails(): void {
    this.showDetailsModal = false;
    this.selectedAuction = null;
  }

  deleteAuction(auction: { id: number }): void {
    if (!confirm('Czy na pewno chcesz usunąć tę aukcję?')) {
      return;
    }
    this.auctionService.deleteAuction(auction.id).subscribe({
      next: () => {
        this.myAuctions = this.myAuctions.filter((a) => a.id !== auction.id);
        this.okMessage = 'Aukcja została usunięta.';
        this.ngOnInit();
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Nie udało się usunąć aukcji.';
        this.cdr.detectChanges();
      },
    });
  }

  getCurrentPrice(a: Auction): number {
    return a.currentBid ?? a.startPrice;
  }
}
