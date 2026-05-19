import { Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ChatRoomStateService } from '../../services/chat-state.service';
import { RegisteredUser } from "../../models/chat-room";

@Component({
  selector: 'app-room-members',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './room-members.html',
  styleUrl: './room-members.css',
})
export class RoomMembers {
  private readonly route = inject(ActivatedRoute);
  private readonly roomsService = inject(ChatRoomStateService);

  protected readonly roomId = signal<number | null>(null);
  protected readonly roomName = computed(() => {
    const id = this.roomId();
    if (id === null) return 'Room';

    return this.roomsService.roomById(id)?.name ?? 'Unknown room';
  });
  protected readonly members = computed(() => {
    const id = this.roomId();
    if (id === null) return [];

    return this.roomsService.members()[id] ?? [];
  });

  username = '';

  constructor() {
    this.route.paramMap.pipe(takeUntilDestroyed()).subscribe((params) => {
      const rawId = params.get('roomId');
      const id = rawId ? Number(rawId) : null;

      if (id === null || Number.isNaN(id)) {
        this.roomId.set(null);
        return;
      }
      this.roomId.set(id);
      if (id !== null) {
        this.roomsService.refreshRoom(id);
      }
    });
  }

  // protected addMember(): void {
  //   const id = this.roomId();
  //   const username = this.username.trim();

  //   if (id === null || !username) {
  //     return;
  //   }

  //   this.roomsService.addMember(id, username);
  //   this.username = '';
  //   this.members.set(this.roomsService.membersForRoom(id));
  // }
}
