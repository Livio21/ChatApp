import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { JoinedRoomsService } from '../../services/joined-rooms.service';

@Component({
  selector: 'app-room-members',
  imports: [RouterLink, FormsModule],
  templateUrl: './room-members.html',
  styleUrl: './room-members.css',
})
export class RoomMembers {
  private readonly route = inject(ActivatedRoute);
  private readonly roomsService = inject(JoinedRoomsService);

  protected readonly roomId = signal<string | null>(null);
  protected readonly roomName = signal<string>('Room');
  protected readonly members = signal<string[]>([]);

  username = '';

  constructor() {
    this.route.paramMap.pipe(takeUntilDestroyed()).subscribe((params) => {
      const id = params.get('roomId');
      this.roomId.set(id);
      const room = id ? this.roomsService.roomById(id) : undefined;
      this.roomName.set(room?.name ?? 'Unknown room');
      if (id) {
        this.members.set(this.roomsService.membersForRoom(id));
      }
    });
  }

  protected addMember(): void {
    const id = this.roomId();
    if (!id) {
      return;
    }
    this.roomsService.addMemberPlaceholder(id, this.username);
    this.username = '';
    this.members.set(this.roomsService.membersForRoom(id));
  }
}
