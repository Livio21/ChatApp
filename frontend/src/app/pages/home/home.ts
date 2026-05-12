import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { JoinedRoomsService } from '../../services/joined-rooms.service';

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  protected readonly roomsService = inject(JoinedRoomsService);
  protected readonly rooms = this.roomsService.rooms;
  protected readonly hasRooms = this.roomsService.hasRooms;

  protected readonly joinCodeInput = signal('');

  protected setJoinCode(value: string): void {
    this.joinCodeInput.set(value);
  }

  protected joinRoom(): void {
    const code = this.joinCodeInput();
    this.roomsService.joinRoomByCode(code);
    this.joinCodeInput.set('');
  }
}
