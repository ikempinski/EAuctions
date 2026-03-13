import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { take } from 'rxjs';
import { environment } from '../environments/environment';
import { User } from '../_models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(private http: HttpClient) {}

  register(email: string, username: string, password: string) {
    return this.http
      .post<User>(
        `${environment.apiUrl}/user/register`,
        { email, username, password },
        { withCredentials: true }
      )
      .pipe(take(1));
  }

  login(email: string, password: string) {
    return this.http
      .post<User>(
        `${environment.apiUrl}/user/login`,
        { email, password },
        { withCredentials: true }
      )
      .pipe(take(1));
  }

  getCurrentUser() {
    return this.http
      .get<User>(`${environment.apiUrl}/user/me`, { withCredentials: true })
      .pipe(take(1));
  }

  updateProfile(email: string, username: string) {
    return this.http
      .put<User>(
        `${environment.apiUrl}/user/update`,
        { email, username },
        { withCredentials: true }
      )
      .pipe(take(1));
  }

  changePassword(oldPassword: string, newPassword: string) {
    return this.http
      .post<void>(
        `${environment.apiUrl}/user/change-password`,
        { oldPassword, newPassword },
        { withCredentials: true }
      )
      .pipe(take(1));
  }

  logout() {
    return this.http
      .post<void>(`${environment.apiUrl}/user/logout`, {}, { withCredentials: true })
      .pipe(take(1));
  }
}
