import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChatRoom ,RegisteredUser,ChatMessage} from "../models/chat-room";

@Injectable({
  providedIn: 'root',
})
export class ChatRoomApiService {
  private readonly http = inject(HttpClient);

  createRoom(room: Partial<ChatRoom>): Observable<void> {
    return this.http.post<void>(`/api/add-room`, room);
  }

  joinRoomById(roomId: number): Observable<void> {
    return this.http.post<void>(`/api/chat-rooms/${roomId}/join`, {});
  }

  getRooms(): Observable<ChatRoom[]> {
    return this.http.get<ChatRoom[]>(`/api/chat-rooms`);
  }

  getRoom(roomId: number): Observable<ChatRoom> {
    return this.http.get<ChatRoom>(`/api/chat-rooms/${roomId}`);
  }
}
