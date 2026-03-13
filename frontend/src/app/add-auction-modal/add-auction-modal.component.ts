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

  titleError = '';
  descriptionError = '';
  startPriceError = '';
  endDateError = '';
  photoError = '';
  errorMessage = '';
  onEndDateChange(input: HTMLInputElement): void {
    input.blur();
  }

  onCancel(): void {
    this.close.emit();
  }

  onSubmit(form: NgForm): void {
    if (this.validateForm()) {
      this.save.emit(this.newAuction);
    }
  }

  validateForm(): boolean {
    var isOk = true;
    if (this.newAuction.title.length < 3) {
      isOk = false;
      this.titleError = 'Tytuł aukcji musi mieć co najmniej 3 znaki';
    } else {
      this.titleError = '';
    }

    if (this.newAuction.description.length < 10) {
      isOk = false;
      this.descriptionError = 'Opis musi mieć co najmniej 10 znaków';
    } else {
      this.descriptionError = '';
    }

    if (this.newAuction.startPrice <= 0) {
      isOk = false;
      this.startPriceError = 'Cena wywoławcza musi być większa od 0';
    } else {
      this.startPriceError = '';
    }

    if (this.newAuction.endDate === '') {
      isOk = false;
      this.endDateError = 'Data zakończenia jest wymagana';
    } else {
      this.endDateError = '';
    }

    if (new Date(this.newAuction.endDate) <= new Date()) {
      isOk = false;
      this.endDateError = 'Data zakończenia musi być większa od dziś';
    } else {
      this.endDateError = '';
    }

    if (this.newAuction.photo === null) {
      isOk = false;
      this.photoError = 'Zdjęcie produktu jest wymagane';
    } else {
      this.photoError = '';
    }

    return isOk;
  }
}

