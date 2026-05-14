import { Component, inject, signal, OnDestroy, computed } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ChatRoomStateService } from '../../services/chat-state.service';
import {
  ChatSocketService,
  ChatMessageType,
  ChatMessagePayload,
} from '../../services/websocket.service';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { AuthService } from "../../services/auth-service";

@Component({
  selector: 'app-room-chat',
  standalone: true,
  imports: [RouterLink, FormsModule, DatePipe],
  templateUrl: './room-chat.html',
  styleUrl: './room-chat.css',
})
export class RoomChat implements OnDestroy {
  private readonly route = inject(ActivatedRoute);
  private readonly roomsService = inject(ChatRoomStateService);
  private readonly socket = inject(ChatSocketService);
  private readonly auth = inject(AuthService);

  protected readonly roomId = signal<number | null>(null);
  protected readonly roomName = computed(() => {
    const id = this.roomId();

    if (id === null) return 'Room';

    return this.roomsService.roomById(id)?.name ?? 'Unknown room';
  });
  protected readonly messages = computed(() => {
    const id = this.roomId();
    if (id === null) return [];

    return this.roomsService.messages()[id] ?? [];
  });
  protected readonly newMessage = signal('');

  constructor() {
    this.route.paramMap.pipe(takeUntilDestroyed()).subscribe((params) => {
      const rawId = params.get('roomId');
      const id = rawId ? Number(rawId) : null;

      if (id !== null && Number.isNaN(id)) {
        this.roomId.set(null);
        return;
      }

      const previousId = this.roomId();

      if (previousId !== null && previousId !== id) {
        this.socket.unsubscribeFromRoom(previousId);
      }

      this.roomId.set(id);

    });
  }

  ngOnDestroy(): void {
    const id = this.roomId();
    if (id !== null) {
      this.socket.unsubscribeFromRoom(id);
    }
  }

  protected sendMessage(): void {
    const text = this.newMessage().trim();
    const id = this.roomId();

    if (!text || id === null) return;

    this.socket.sendMessage(id, {
      message: text,
      sender: this.auth.getUsername(),
      creationDate: new Date().toISOString(),
      messageType: ChatMessageType.CHAT_MESSAGE,
    });

    this.newMessage.set('');
  }
}
