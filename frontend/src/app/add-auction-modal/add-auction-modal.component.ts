import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';

export interface AddAuctionPayload {
  title: string;
  description: string;
  startPrice: number;
  endDate: string;
  photo: File | null;
}

@Component({
  selector: 'app-add-auction-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-auction-modal.component.html',
})
export class AddAuctionModalComponent {
  @Output() close = new EventEmitter<void>();
  @Output() save = new EventEmitter<AddAuctionPayload>();

  newAuction: AddAuctionPayload = {
    title: '',
    description: '',
    startPrice: 0,
    endDate: '',
    photo: null,
  };

  onEndDateChange(input: HTMLInputElement): void {
    input.blur();
  }

  onCancel(): void {
    this.close.emit();
  }

  onSubmit(form: NgForm): void {
    if (!form.valid) {
      return;
    }
    this.save.emit(this.newAuction);
  }
}

