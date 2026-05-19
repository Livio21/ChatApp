import { Injectable, computed, signal } from '@angular/core';
import { ChatSocketService } from './websocket.service';
import { ChatRoomApiService } from './chat.service';
import { ChatMessage, ChatRoom, RegisteredUser } from "../models/chat-room";

@Injectable({
  providedIn: 'root',
})
export class ChatRoomStateService {
  private readonly _rooms = signal<ChatRoom[]>([]);
  private readonly _members = signal<Record<number, RegisteredUser[]>>({});
  private readonly _messages = signal<Record<number, ChatMessage[]>>({});
  readonly rooms = this._rooms.asReadonly();
  readonly messages = this._messages.asReadonly();
  readonly members = this._members.asReadonly();
  readonly hasRooms = computed(() => this._rooms().length > 0);

  constructor(
    private api: ChatRoomApiService,
    private socket: ChatSocketService,
  ) {
    this.socket.messages$.subscribe(({ roomId, payload }) => {
      // Sync members from DB on JOIN/LEAVE
      if (payload.messageType === 'JOIN' || payload.messageType === 'LEAVE') {
        this.refreshRoom(roomId);
      }

      this._messages.update((state) => {
        const currentMessages = state[roomId] ?? [];
        // Avoid duplicates if message ID exists (using explicit null/undefined check for ID 0)
        const hasId = payload.id !== undefined && payload.id !== null;
        if (hasId && currentMessages.some((m) => m.id === payload.id)) {
          return state;
        }
        return {
          ...state,
          [roomId]: [...currentMessages, payload as ChatMessage],
        };
      });
    });
  }

  roomById(id: number): ChatRoom | undefined {
    return this._rooms().find((r) => r.id === id);
  }

  loadRooms(): void {
    this.api.getRooms().subscribe((rooms) => {
      const normalizedRooms = rooms.map((room) => this.normalizeRoom(room));
      this._rooms.set(normalizedRooms);
      this._members.set(
        normalizedRooms.reduce((state, room) => ({
          ...state,
          [room.id]: room.registeredUsers ?? [],
        }), {} as Record<number, RegisteredUser[]>),
      );
      this._messages.set(
        normalizedRooms.reduce((state, room) => ({
          ...state,
          [room.id]: room.chatMessages ?? [],
        }), {} as Record<number, ChatMessage[]>),
      );
    });
  }

  createRoom(payload: { name: string; description: string }): void {
    this.api.createRoom(payload).subscribe({
      next: () => this.loadRooms(),
      error: (err) => console.error('Failed to create room', err),
    });
  }

  joinRoomById(roomId: number): void {
    this.api.joinRoomById(roomId).subscribe({
      next: () => this.loadRooms(),
      error: (err) => console.error('Failed to join room', err),
    });
  }

  refreshRoom(roomId: number): void {
    this.api.getRoom(roomId).subscribe((room) => {
      this.addRoomToState(this.normalizeRoom(room));
    });
  }

  openRoom(roomId: number): void {
    this.api.getRoom(roomId).subscribe((room) => {
      this.addRoomToState(this.normalizeRoom(room));
      this.socket.subscribeToRoom(roomId);
    });
  }

  private addRoomToState(room: ChatRoom): void {
    this._rooms.update((list) => {
      const index = list.findIndex((r) => r.id === room.id);
      if (index > -1) {
        const newList = [...list];
        newList[index] = room;
        return newList;
      }
      return [...list, room];
    });

    this._members.update((state) => ({
      ...state,
      [room.id]: room.registeredUsers ?? [],
    }));

    this._messages.update((state) => {
      const existing = state[room.id] ?? [];
      const incoming = room.chatMessages ?? [];
      const combined = [...existing];

      incoming.forEach((msg) => {
        const hasId = msg.id !== undefined && msg.id !== null;
        const isDuplicate = hasId 
          ? combined.some((m) => m.id === msg.id)
          : combined.some((m) => m.message === msg.message && m.creationDate === msg.creationDate);

        if (!isDuplicate) {
          combined.push(msg);
        }
      });

      combined.sort((a, b) => {
        const dateA = a.creationDate ? new Date(a.creationDate).getTime() : 0;
        const dateB = b.creationDate ? new Date(b.creationDate).getTime() : 0;
        return dateA - dateB;
      });

      return {
        ...state,
        [room.id]: combined,
      };
    });
  }

  addMember(roomId: number, user: RegisteredUser): void {
    this._members.update((m) => {
      const current = m[roomId] ?? [];

      if (current.some((u) => u.id === user.id)) return m;

      return {
        ...m,
        [roomId]: [...current, user],
      };
    });
  }

  removeMember(roomId: number, userId: number): void {
    this._members.update((m) => {
      const current = m[roomId] ?? [];

      return {
        ...m,
        [roomId]: current.filter((u) => u.id !== userId),
      };
    });
  }

  membersForRoom(roomId: number): RegisteredUser[] {
    return this._members()[roomId] ?? [];
  }

  private normalizeRoom(room: any): ChatRoom {
    return {
      id: room.id,
      name: room.name,
      description: room.description,
      ownerId: String(room.ownerId ?? room.ownerId ?? ''),
      registeredUsers: room.registeredUsers ?? room.users ?? [],
      chatMessages: room.chatMessages ?? room.messages ?? [],
    };
  }
}
