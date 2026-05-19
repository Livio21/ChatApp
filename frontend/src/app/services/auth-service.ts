import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { Jwt } from '../models/jwt';
import { SignInRequest } from '../models/sign-in-request';
import { SignUpRequest } from '../models/sign-up-request';

const TOKEN_KEY = 'access_token';
const USERNAME = 'user_name';
const EXPIRATION = 'expiration_date';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);

  private readonly baseUrl = '/auth';

  getStoredToken(): string | null {
    const expiration = localStorage.getItem(EXPIRATION);

    if (expiration) {
      const expirationTime = new Date(expiration).getTime();

      if (Date.now() >= expirationTime) {
        this.clearToken();
        return null;
      }
    }

    return localStorage.getItem(TOKEN_KEY);
  }

  getUsername(): string | null {
    return localStorage.getItem(USERNAME);
  }

  clearToken(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USERNAME);
    localStorage.removeItem(EXPIRATION);
  }

  signIn(req: SignInRequest): Observable<Jwt> {
    return this.http.post<Jwt>(`${this.baseUrl}/sign-in`, req).pipe(
      tap((res) => {
        if (res?.token) {
          localStorage.setItem(TOKEN_KEY, res.token);
          localStorage.setItem(USERNAME, res.username);
          localStorage.setItem(EXPIRATION, res.expiration);
        }
      }),
    );
  }

  signUp(req: SignUpRequest): Observable<Jwt> {
    return this.http.post<Jwt>(`${this.baseUrl}/sign-up`, req).pipe(
      tap((res) => {
        if (res?.token) {
          localStorage.setItem(TOKEN_KEY, res.token);
          localStorage.setItem(USERNAME, res.username);

          if (res.expiration) {
            localStorage.setItem(EXPIRATION, res.expiration);
          }
        }
      }),
    );
  }
}
