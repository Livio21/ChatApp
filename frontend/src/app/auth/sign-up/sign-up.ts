import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth-service';
import { ChatRoomStateService } from '../../services/chat-state.service';
import { ChatSocketService } from '../../services/websocket.service';

@Component({
  selector: 'app-sign-up',
  imports: [FormsModule, RouterLink],
  templateUrl: './sign-up.html',
  styleUrl: './sign-up.css',
})
export class SignUp {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly roomsService = inject(ChatRoomStateService);
  private readonly socket = inject(ChatSocketService);

  username = '';
  email = '';
  password = '';
  protected readonly error = signal<string | null>(null);
  protected readonly loading = signal(false);

  protected submit(): void {
    this.error.set(null);
    this.loading.set(true);
    this.auth
      .signUp({ username: this.username, email: this.email, password: this.password })
      .subscribe({
        next: () => {
          const token = this.auth.getStoredToken();
          if (token) {
            this.socket.connect(token);
          }
          this.roomsService.loadRooms();
          this.router.navigate(['/home']);
        },
        error: (err: { error?: { message?: string }; message?: string; status?: number }) => {
          const body = err.error;
          const msg =
            (typeof body === 'object' && body && 'message' in body && typeof body.message === 'string'
              ? body.message
              : null) ??
            err.message ??
            (err.status != null ? `Request failed (${err.status})` : 'Sign-up failed');
          this.error.set(msg);
          this.loading.set(false);
        },
        complete: () => this.loading.set(false),
      });
  }
}
