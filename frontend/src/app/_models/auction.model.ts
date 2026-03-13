export interface Auction {
  id: number;
  title: string;
  description: string;
  startPrice: number;
  endDate: string;
  photoName: string;
  currentBid?: number;
  ownerUsername?: string;
  myCurrentBid?: boolean;
  highestBidderUsername?: string;
}

