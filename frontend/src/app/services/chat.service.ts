import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChatRoom ,RegisteredUser,ChatMessage} from "../models/chat-room";

@Injectable({
  providedIn: 'root',
})
export class ChatRoomApiService {
  private readonly http = inject(HttpClient);

  createRoom(payload: { name: string; description: string }): Observable<ChatRoom> {
    return this.http.post<ChatRoom>(`/api/add-room`, payload);
  }

  joinRoomById(roomId: number): Observable<ChatRoom> {
    return this.http.post<ChatRoom>(`/api/chat-rooms/${roomId}/join`, {});
  }

  getRooms(): Observable<ChatRoom[]> {
    return this.http.get<ChatRoom[]>(`/api/chat-rooms`);
  }

  getRoom(roomId: number): Observable<ChatRoom> {
    return this.http.get<ChatRoom>(`/api/chat-rooms/${roomId}`);
  }
}
