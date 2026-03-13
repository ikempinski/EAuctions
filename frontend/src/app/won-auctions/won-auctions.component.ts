import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuctionService } from '../_services/auction.service';

@Component({
  selector: 'app-won-auctions',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './won-auctions.component.html'
})
export class WonAuctionsComponent implements OnInit {
  wonAuctions: { title: string; finalPrice: number; photoName?: string }[] = [];

  constructor(private auctionService: AuctionService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.auctionService.getMyWonAuctions().subscribe({
      next: (list) => {
        this.wonAuctions = list.map((w) => ({
          title: w.title,
          finalPrice: w.finalPrice,
          photoName: w.photoName,
        }));
        this.cdr.detectChanges();
      },
    });
  }
}
