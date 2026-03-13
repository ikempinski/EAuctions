import { Routes } from '@angular/router';
import { GuestViewComponent } from './guest-view/guest-view.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { AccountComponent } from './account/account.component';
import { AccountDetailsComponent } from './account-details/account-details.component';
import { MyAuctionsComponent } from './my-auctions/my-auctions.component';
import { WonAuctionsComponent } from './won-auctions/won-auctions.component';
import { authGuard } from './_services/auth.guard';

export const routes: Routes = [
  { path: '', component: GuestViewComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'account',
    component: AccountComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'details', pathMatch: 'full' },
      { path: 'details', component: AccountDetailsComponent },
      { path: 'my-auctions', component: MyAuctionsComponent },
      { path: 'won-auctions', component: WonAuctionsComponent },
    ],
  },
  { path: 'guest-view', component: GuestViewComponent },
];
