import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ChatRoomStateService } from '../../services/chat-state.service';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  protected readonly roomsService = inject(ChatRoomStateService);
  protected readonly rooms = this.roomsService.rooms;
  protected readonly hasRooms = this.roomsService.hasRooms;

  protected readonly joinCodeInput = signal('');

  protected setJoinCode(value: string): void {
    this.joinCodeInput.set(value);
  }

  protected joinRoom(): void {
    const roomId = Number(this.joinCodeInput);
    if (!roomId) {
      return;
    }

    this.roomsService.joinRoomById(roomId);

    this.joinCodeInput.set('');
  }
}
