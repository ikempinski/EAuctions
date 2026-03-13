import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { take } from 'rxjs';
import { environment } from '../environments/environment';
import type { Auction } from '../_models/auction.model';

@Injectable({ providedIn: 'root' })
export class AuctionService {
  constructor(private http: HttpClient) {}

  /**
   * Creates a new auction with optional photo upload.
   * Expects backend endpoint /auction/create that accepts multipart/form-data.
   */
  createAuction(payload: {
    title: string;
    description: string;
    startPrice: number;
    endDate: string;
    photo: File | null;
  }) {
    const formData = new FormData();
    formData.append('title', payload.title);
    formData.append('description', payload.description);
    formData.append('startPrice', String(payload.startPrice));
    formData.append('endDate', payload.endDate);
    if (payload.photo) {
      formData.append('photo', payload.photo, payload.photo.name);
    }

    return this.http
      .post<Auction>(`${environment.apiUrl}/auction/create`, formData, {
        withCredentials: true,
      })
      .pipe(take(1));
  }

  getMyAuctions() {
    return this.http
      .get<Auction[]>(`${environment.apiUrl}/auction/my`, {
        withCredentials: true,
      })
      .pipe(take(1));
  }

  deleteAuction(id: number) {
    return this.http
      .post<void>(`${environment.apiUrl}/auction/delete/${id}`, {})
      .pipe(take(1));
  }

  placeBid(auctionId: number, amount: number) {
    return this.http
      .post<number>(
        `${environment.apiUrl}/auction/bid/${auctionId}`,
        {},
        {
          params: { amount: String(amount) },
          withCredentials: true,
        }
      )
      .pipe(take(1));
  }

  getGuestAuctions() {
    return this.http
      .get<Auction[]>(`${environment.apiUrl}/auction/guest`)
      .pipe(take(1));
  }

  getMyWonAuctions() {
    return this.http
      .get<{ auctionId: number; title: string; finalPrice: number; endDate: string; photoName?: string }[]>(
        `${environment.apiUrl}/auction/won`,
        { withCredentials: true }
      )
      .pipe(take(1));
  }
}
