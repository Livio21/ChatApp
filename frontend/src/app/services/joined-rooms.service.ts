import { Injectable, computed, signal } from '@angular/core';
import { JoinedRoom } from '../models/joined-room';

@Injectable({
  providedIn: 'root',
})
export class JoinedRoomsService {
  private readonly _rooms = signal<JoinedRoom[]>([]);
  private readonly _membersByRoom = signal<Record<string, string[]>>({});

  readonly rooms = this._rooms.asReadonly();
  readonly hasRooms = computed(() => this._rooms().length > 0);

  roomById(id: string): JoinedRoom | undefined {
    return this._rooms().find((r) => r.id === id);
  }

  membersForRoom(roomId: string): string[] {
    return this._membersByRoom()[roomId] ?? ['You'];
  }

  joinRoomByCode(code: string): void {
    const trimmed = code.trim();
    if (!trimmed) {
      return;
    }
    const id = `room-${trimmed.toLowerCase().replace(/\s+/g, '-')}`;
    if (this._rooms().some((r) => r.id === id)) {
      return;
    }
    const name = `Room · ${trimmed}`;
    this._rooms.update((list) => [...list, { id, name }]);
    this._membersByRoom.update((m) => ({
      ...m,
      [id]: ['You', 'Alex', 'Jamie'],
    }));
  }

  addMemberPlaceholder(roomId: string, username: string): void {
    const u = username.trim();
    if (!u) {
      return;
    }
    this._membersByRoom.update((m) => {
      const current = m[roomId] ?? ['You'];
      if (current.includes(u)) {
        return m;
      }
      return { ...m, [roomId]: [...current, u] };
    });
  }
}
