import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth-service';
import { JoinedRoomsService } from '../../services/joined-rooms.service';

@Component({
  selector: 'app-sidebar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar {
  private readonly auth = inject(AuthService);
  private readonly roomsService = inject(JoinedRoomsService);
  private readonly router = inject(Router);

  protected readonly rooms = this.roomsService.rooms;
  protected readonly joinCodeInput = signal('');
  // component.ts
  openBar = signal(false);

  toggleSidebar() {
    this.openBar.update((v) => !v);
  }

  protected setJoinCode(value: string): void {
    this.joinCodeInput.set(value);
  }

  protected joinRoom(): void {
    const code = this.joinCodeInput();
    this.roomsService.joinRoomByCode(code);
    this.joinCodeInput.set('');
  }

  protected logout(): void {
    this.auth.clearToken();
    void this.router.navigate(['/sign-in']);
  }
}
