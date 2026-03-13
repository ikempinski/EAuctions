import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-won-auctions',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './won-auctions.component.html'
})
export class WonAuctionsComponent {
  wonAuctions: { title: string; finalPrice: number }[] = [];
}
