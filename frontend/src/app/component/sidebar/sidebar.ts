import { Component, computed, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth-service';
import { ChatRoomStateService } from '../../services/chat-state.service';

type RoomForm = {
  name: string;
  description: string;
  owner:string;
};

@Component({
  selector: 'app-sidebar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar {
  private readonly auth = inject(AuthService);
  private readonly roomsService = inject(ChatRoomStateService);
  private readonly router = inject(Router);

  protected readonly currentUsername = computed(() => this.auth.getUsername());
  protected readonly rooms = this.roomsService.rooms;
  protected readonly joinCodeInput = signal('');

  protected readonly roomValues = signal<RoomForm>({
    name: '',
    description: '',
    owner:'',
  });

  openBar = signal(false);

  toggleSidebar() {
    this.openBar.update((v) => !v);
  }

  protected setJoinCode(value: string): void {
    this.joinCodeInput.set(value);
  }

  protected setRoomName(name: string): void {
    this.roomValues.update((v) => ({
      ...v,
      name,
    }));
  }

  protected setRoomDescription(description: string): void {
    this.roomValues.update((v) => ({
      ...v,
      description,
    }));
  }

  protected joinRoom(): void {
    const code = Number(this.joinCodeInput())
    if (!code) return;

    this.roomsService.joinRoomById(code);
    this.joinCodeInput.set('');
  }

  protected createRoom(): void {
    const values = this.roomValues();
    if (!values.name.trim()) return;

    this.roomsService.createRoom(
      {
        name: values.name,
        description: values.description,
      },
    );

    this.roomValues.set({
      name: '',
      description: '',
      owner:'',
    });
  }

  protected logout(): void {
    this.auth.clearToken();
    void this.router.navigate(['/sign-in']);
  }
}
