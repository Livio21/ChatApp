import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { JoinedRoomsService } from '../../services/joined-rooms.service';

interface PlaceholderMessage {
  id: string;
  author: string;
  preview: string;
  time: string;
}

@Component({
  selector: 'app-room-chat',
  imports: [RouterLink],
  templateUrl: './room-chat.html',
  styleUrl: './room-chat.css',
})
export class RoomChat {
  private readonly route = inject(ActivatedRoute);
  private readonly roomsService = inject(JoinedRoomsService);

  protected readonly roomId = signal<string | null>(null);
  protected readonly roomName = signal<string>('Room');
  protected readonly messages = signal<PlaceholderMessage[]>([
    { id: '1', author: 'Alex', preview: 'Placeholder: API not wired yet.', time: '10:02' },
    {
      id: '2',
      author: 'Jamie',
      preview: 'Sounds good — will sync when endpoints exist.',
      time: '10:04',
    },
    { id: '3', author: 'You', preview: 'Mock message thread for layout only.', time: '10:05' },
    { id: '3', author: 'You', preview: 'Mock message thread for layout only.', time: '10:05' },
  ]);

  constructor() {
    this.route.paramMap.pipe(takeUntilDestroyed()).subscribe((params) => {
      const id = params.get('roomId');
      this.roomId.set(id);
      const room = id ? this.roomsService.roomById(id) : undefined;
      this.roomName.set(room?.name ?? 'Unknown room');
    });
  }
}
