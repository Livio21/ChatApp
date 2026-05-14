import { inject, Injectable } from '@angular/core';
import { Client, IMessage, StompConfig, StompSubscription } from '@stomp/stompjs';
import { Observable, Subject } from 'rxjs';
import { AuthService } from "./auth-service";

export enum ChatMessageType {
  CHAT_MESSAGE = 'CHAT_MESSAGE',
  JOIN = 'JOIN',
  LEAVE = 'LEAVE',
}

export interface ChatMessagePayload {
  message?: string;
  sender?: string | null;
  creationDate?: string;
  messageType: ChatMessageType;
}

@Injectable({
  providedIn: 'root',
})
export class ChatSocketService {

  private readonly auth = inject(AuthService);

  private client!: Client;
  private isConnected = false;
  private roomSubscriptions = new Map<number, StompSubscription>();
  private incoming$ = new Subject<{ roomId: number; payload: ChatMessagePayload }>();

  public get messages$(): Observable<{ roomId: number; payload: ChatMessagePayload }> {
    return this.incoming$.asObservable();
  }

  connect(token: string | null): void {
    if (this.client) {
      this.client.deactivate();
    }

    this.client = new Client({
      brokerURL: 'ws://localhost:8081/ws',
      reconnectDelay: 5000,
      connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
      onConnect: () => console.log('CONNECTED'),
    });

    this.client.activate();
  }

  disconnect(): void {
    if (!this.client) return;

    this.roomSubscriptions.forEach((sub) => sub.unsubscribe());
    this.roomSubscriptions.clear();
    this.client.deactivate();
    this.isConnected = false;
  }

  subscribeToRoom(roomId: number): void {
    if (!this.client) {
      console.warn('WebSocket client not initialized');
      return;
    }

    if (this.roomSubscriptions.has(roomId)) {
      return;
    }

    const subscription = this.client.subscribe(`/topic/messages/${roomId}`, (msg: IMessage) => {
      try {
        const payload: ChatMessagePayload = JSON.parse(msg.body);
        this.incoming$.next({ roomId, payload });
      } catch (e) {
        console.error('Failed to parse incoming message', e);
      }
    });

    this.roomSubscriptions.set(roomId, subscription);

    this.sendMessage(roomId, {
      sender: this.auth.getUsername(),
      creationDate: new Date().toISOString(),
      messageType: ChatMessageType.JOIN,
    });
  }

  unsubscribeFromRoom(roomId: number): void {
    const sub = this.roomSubscriptions.get(roomId);
    if (!sub) return;

    sub.unsubscribe();
    this.roomSubscriptions.delete(roomId);

    this.sendMessage(roomId, {
      sender: this.auth.getUsername(),
      creationDate: new Date().toISOString(),
      messageType: ChatMessageType.LEAVE,
    });
  }

  sendMessage(roomId: number, payload: ChatMessagePayload): void {
    if (!this.client || !this.isConnected) {
      console.warn('Cannot send message, WebSocket not connected');
      return;
    }

    this.client.publish({
      destination: `/topic/messages/${roomId}`,
      body: JSON.stringify(payload),
    });
  }
}
