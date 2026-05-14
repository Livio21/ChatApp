import { Injectable, computed, signal } from '@angular/core';
import { ChatSocketService } from './websocket.service';
import { ChatRoomApiService } from './chat.service';
import { ChatMessage, ChatRoom, RegisteredUser } from "../models/chat-room";
import { forkJoin } from "rxjs";

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
   this._messages.update((state) => ({
     ...state,
     [roomId]: [...(state[roomId] ?? []), payload as ChatMessage],
   }));
 });

  }

  roomById(id: number): ChatRoom | undefined {
    return this._rooms().find((r) => r.id === id);
  }

  loadRooms(): void {
    this.api.getRooms().subscribe((rooms) => {
      rooms.forEach((room) => {
        this.addRoomToState(room);
      });
    });
  }

  createRoom(payload: { name: string; description: string }): void {
    this.api.createRoom(payload).subscribe((room) => {
      this.addRoomToState(room);
    });
  }

  joinRoomById(roomId: number): void {
    this.api.joinRoomById(roomId).subscribe((room) => {
      this.addRoomToState(room);
    });
  }

  private addRoomToState(room: ChatRoom): void {
    const exists = this._rooms().some((r) => r.id === room.id);

    if (exists) return;

    this._rooms.update((list) => [...list, room]);

    this._members.update((state) => ({
      ...state,
      [room.id]: room.registeredUsers ?? [],
    }));

    this._messages.update((state) => ({
      ...state,
      [room.id]: room.chatMessages ?? [],
    }));

    this.socket.subscribeToRoom(room.id);
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
}
