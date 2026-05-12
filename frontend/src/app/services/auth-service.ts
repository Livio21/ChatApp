import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { Jwt } from '../models/jwt';
import { SignInRequest } from '../models/sign-in-request';
import { SignUpRequest } from '../models/sign-up-request';

const TOKEN_KEY = 'access_token';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  /** Dev server proxies /api → http://localhost:8080 (see proxy.conf.json). */
  private readonly baseUrl = '/api';

  getStoredToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  clearToken(): void {
    localStorage.removeItem(TOKEN_KEY);
  }

  signIn(req: SignInRequest): Observable<Jwt> {
    return this.http.post<Jwt>(`${this.baseUrl}/auth/sign-in`, req).pipe(
      tap((res) => {
        if (res?.token) {
          localStorage.setItem(TOKEN_KEY, res.token);
        }
      }),
    );
  }

  signUp(req: SignUpRequest): Observable<Jwt> {
    return this.http.post<Jwt>(`${this.baseUrl}/auth/sign-up`, req).pipe(
      tap((res) => {
        if (res?.token) {
          localStorage.setItem(TOKEN_KEY, res.token);
        }
      }),
    );
  }
}
