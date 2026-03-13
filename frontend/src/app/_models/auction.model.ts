export interface Auction {
  id: number;
  title: string;
  description: string;
  startPrice: number;
  endDate: string;
  photoName: string;
  currentBid?: number;
  isActive?: number;
  ownerUsername?: string;
  myCurrentBid?: boolean;
  highestBidderUsername?: string;
  finished?: boolean;
  winnerUsername?: string;
  winnerEmail?: string;
  finalPrice?: number;
}

